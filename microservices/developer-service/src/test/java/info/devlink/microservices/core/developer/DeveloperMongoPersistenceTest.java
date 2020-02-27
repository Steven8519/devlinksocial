package info.devlink.microservices.core.developer;

import info.devlink.microservices.core.developer.domain.Developer;
import info.devlink.microservices.core.developer.repository.DeveloperRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RunWith(SpringRunner.class)
@DataMongoTest
public class DeveloperMongoPersistenceTest {
    @Autowired
    private DeveloperRepository repository;

    private Developer savedDeveloper;

    @Before
    public void setupDb() {
        repository.deleteAll();

        Developer developer = new Developer(1, "Michael", "Turner", "Reactjs Developer");
        savedDeveloper = repository.save(developer);

        assertEqualsDeveloper(developer, savedDeveloper);
    }

    @Test
    public void create() {

        Developer newEntity = new Developer(2, "Chris", "Mathis", "Python Developer");
        repository.save(newEntity);

        Developer foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsDeveloper(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void update() {
        savedDeveloper.setLastName("Mathis");
        repository.save(savedDeveloper);

        Developer foundEntity = repository.findById(savedDeveloper.getId()).get();
        assertEquals(1, (long) foundEntity.getVersion());
        assertEquals("Mathis", foundEntity.getLastName());
    }

    @Test
    public void delete() {
        repository.delete(savedDeveloper);
        assertFalse(repository.existsById(savedDeveloper.getId()));
    }

    @Test
    public void getByDeveloperId() {
        Optional<Developer> entity = repository.findByDeveloperId(savedDeveloper.getDeveloperId());

        assertTrue(entity.isPresent());
        assertEqualsDeveloper(savedDeveloper, entity.get());
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateError() {
        Developer entity = new Developer(savedDeveloper.getDeveloperId(), "Chris", "Mathis", "Python Developer");
        repository.save(entity);
    }

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        Developer entity1 = repository.findById(savedDeveloper.getId()).get();
        Developer entity2 = repository.findById(savedDeveloper.getId()).get();

        // Update the entity using the first entity object
        entity1.setLastName("Hill");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setLastName("James");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        Developer updatedEntity = repository.findById(savedDeveloper.getId()).get();
        assertEquals(1, (int) updatedEntity.getVersion());
        assertEquals("Hill", updatedEntity.getLastName());
    }

    @Test
    public void paging() {

        repository.deleteAll();

        List<Developer> newDevelopers = rangeClosed(1001, 1010)
                .mapToObj(i -> new Developer(i, "Michael " , "Turner", "Reactjs Developer"))
                .collect(Collectors.toList());
        repository.saveAll(newDevelopers);

        Pageable nextPage = PageRequest.of(0, 4, ASC, "developerId");
        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
    }

    private Pageable testNextPage(Pageable nextPage, String expectedDeveloperIds, boolean expectsNextPage) {
        Page<Developer> developerPage = repository.findAll(nextPage);
        assertEquals(expectedDeveloperIds, developerPage.getContent().stream().map(p -> p.getDeveloperId()).collect(Collectors.toList()).toString());
        assertEquals(expectsNextPage, developerPage.hasNext());
        return developerPage.nextPageable();
    }

    private void assertEqualsDeveloper(Developer expectedEntity, Developer actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getDeveloperId(),        actualEntity.getDeveloperId());
        assertEquals(expectedEntity.getFirstName(),           actualEntity.getFirstName());
        assertEquals(expectedEntity.getLastName(),           actualEntity.getLastName());
        assertEquals(expectedEntity.getDeveloperType(),      actualEntity.getDeveloperType());
    }
}
