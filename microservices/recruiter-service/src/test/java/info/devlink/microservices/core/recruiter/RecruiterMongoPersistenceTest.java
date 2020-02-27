package info.devlink.microservices.core.recruiter;

import info.devlink.microservices.core.recruiter.domain.RecruiterEntity;
import info.devlink.microservices.core.recruiter.repository.RecruiterRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecruiterMongoPersistenceTest {
    @Autowired
    private RecruiterRepository repository;

    private RecruiterEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        RecruiterEntity entity = new RecruiterEntity(1, 2, "Allyson Penzinski", "Job Spring Partners", "C");
        savedEntity = repository.save(entity);

        assertEqualsRecruiter(entity, savedEntity);
    }


    @Test
    public void create() {

        RecruiterEntity newEntity = new RecruiterEntity(1, 3, "Tamara Stewart", "Judge Group", "B");
        repository.save(newEntity);

        RecruiterEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsRecruiter(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void update() {
        savedEntity.setRecruiterName("Malissa Torres");
        repository.save(savedEntity);

        RecruiterEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long) foundEntity.getVersion());
        assertEquals("Malissa Torres", foundEntity.getRecruiterName());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
    public void getByProductId() {
        List<RecruiterEntity> entityList = repository.findByDeveloperId(savedEntity.getDeveloperId());

        assertThat(entityList, hasSize(1));
        assertEqualsRecruiter(savedEntity, entityList.get(0));
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateError() {
        RecruiterEntity entity = new RecruiterEntity(1, 2, "Tamara Stewart", "Judge Group", "B");
        repository.save(entity);
    }

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        RecruiterEntity entity1 = repository.findById(savedEntity.getId()).get();
        RecruiterEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setRecruiterName("Malissa Torres");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setRecruiterName("Sam Willie");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        RecruiterEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("Malissa Torres", updatedEntity.getRecruiterName());
    }

    private void assertEqualsRecruiter(RecruiterEntity expectedEntity, RecruiterEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getDeveloperId(),        actualEntity.getDeveloperId());
        assertEquals(expectedEntity.getRecruiterId(), actualEntity.getRecruiterId());
        assertEquals(expectedEntity.getRecruiterName(),           actualEntity.getRecruiterName());
        assertEquals(expectedEntity.getRecruitingCompany(),           actualEntity.getRecruitingCompany());
        assertEquals(expectedEntity.getCompanyRating(),          actualEntity.getCompanyRating());
    }
}
