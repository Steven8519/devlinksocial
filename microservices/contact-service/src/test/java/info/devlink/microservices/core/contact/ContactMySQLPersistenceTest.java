package info.devlink.microservices.core.contact;

import info.devlink.microservices.core.contact.domain.ContactEntity;
import info.devlink.microservices.core.contact.repository.ContactRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class ContactMySQLPersistenceTest {
    @Autowired
    private ContactRepository repository;

    private ContactEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        ContactEntity entity = new ContactEntity(1, 2, 3, "sample@gmail.com", "2145554444");
        savedEntity = repository.save(entity);

        assertEqualsContact(entity, savedEntity);
    }


    @Test
    public void create() {

        ContactEntity newEntity = new ContactEntity(1, 3, 4, "example@gmail.com", "3134445555");
        repository.save(newEntity);

        ContactEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsContact(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void update() {
        savedEntity.setEmail("example2@gmail.com");
        repository.save(savedEntity);

        ContactEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("example2@gmail.com", foundEntity.getEmail());
    }

    @Test
    public void delete() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
    public void getByDeveloperId() {
        List<ContactEntity> entityList = repository.findByDeveloperId(savedEntity.getDeveloperId());

        assertThat(entityList, hasSize(1));
        assertEqualsContact(savedEntity, entityList.get(0));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void duplicateError() {
        ContactEntity entity = new ContactEntity(1, 2, 3, "sample@gmail.com", "2145554444");
        repository.save(entity);
    }

    @Test
    public void optimisticLockError() {

        // Store the saved entity in two separate entity objects
        ContactEntity entity1 = repository.findById(savedEntity.getId()).get();
        ContactEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setEmail("example1@gmail.com");
        repository.save(entity1);

        //  Update the entity using the second entity object.
        // This should fail since the second entity now holds a old version number, i.e. a Optimistic Lock Error
        try {
            entity2.setEmail("example2@gmail.com");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        // Get the updated entity from the database and verify its new sate
        ContactEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("example1@gmail.com", updatedEntity.getEmail());
    }

    private void assertEqualsContact(ContactEntity expectedEntity, ContactEntity actualEntity) {
        assertEquals(expectedEntity.getId(),        actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),   actualEntity.getVersion());
        assertEquals(expectedEntity.getDeveloperId(), actualEntity.getDeveloperId());
        assertEquals(expectedEntity.getContactId(),  actualEntity.getContactId());
        assertEquals(expectedEntity.getEmail(),    actualEntity.getEmail());
        assertEquals(expectedEntity.getPhoneNumber(),   actualEntity.getPhoneNumber());
    }
}
