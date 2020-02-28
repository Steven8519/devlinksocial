package info.devlink.microservices.core.developer;

import info.devlink.microservices.core.developer.domain.DeveloperEntity;
import info.devlink.microservices.core.developer.repository.DeveloperRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@DataMongoTest
public class DeveloperMongoPersistenceTest {
    @Autowired
    private DeveloperRepository repository;

    private DeveloperEntity savedEntity;

    @Before
    public void setupDb() {
        StepVerifier.create(repository.deleteAll()).verifyComplete();

        DeveloperEntity entity = new DeveloperEntity(1, "Michael", "Williams", "Java Developer");
        StepVerifier.create(repository.save(entity))
                .expectNextMatches(createdEntity -> {
                    savedEntity = createdEntity;
                    return areDeveloperEqual(entity, savedEntity);
                })
                .verifyComplete();
    }




    @Test
    public void update() {
        savedEntity.setLastName("Williams");
        StepVerifier.create(repository.save(savedEntity))
                .expectNextMatches(updatedEntity -> updatedEntity.getLastName().equals("Williams"))
                .verifyComplete();

        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getLastName().equals("Williams"))
                .verifyComplete();
    }

    @Test
    public void delete() {
        StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
        StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
    }

    @Test
    public void duplicateError() {
        DeveloperEntity entity = new DeveloperEntity(savedEntity.getDeveloperId(),"Michael", "Williams", "Java Developer");
        StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
    }

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        DeveloperEntity entity1 = repository.findById(savedEntity.getId()).block();
        DeveloperEntity entity2 = repository.findById(savedEntity.getId()).block();

        // Update the entity using the first entity object
        entity1.setLastName("Turner");
        repository.save(entity1).block();

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

        // Get the updated entity from the database and verify its new sate
        StepVerifier.create(repository.findById(savedEntity.getId()))
                .expectNextMatches(foundEntity ->
                        foundEntity.getVersion() == 1 &&
                                foundEntity.getLastName().equals("Turner"))
                .verifyComplete();
    }

    private boolean areDeveloperEqual(DeveloperEntity expectedEntity, DeveloperEntity actualEntity) {
        return
                (expectedEntity.getId().equals(actualEntity.getId())) &&
                        (expectedEntity.getVersion() == actualEntity.getVersion()) &&
                        (expectedEntity.getDeveloperId() == actualEntity.getDeveloperId()) &&
                        (expectedEntity.getFirstName().equals(actualEntity.getFirstName())) &&
                        (expectedEntity.getLastName() == actualEntity.getLastName());
    }
}
