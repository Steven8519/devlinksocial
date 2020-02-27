package info.devlink.microservices.core.contact.repository;

import info.devlink.microservices.core.contact.domain.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactRepository extends CrudRepository<Contact, Integer> {

    @Transactional(readOnly = true)
    List<Contact> findByDeveloperId(int developerId);
}
