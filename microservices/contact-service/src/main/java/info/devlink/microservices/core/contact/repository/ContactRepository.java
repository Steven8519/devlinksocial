package info.devlink.microservices.core.contact.repository;

import info.devlink.microservices.core.contact.domain.ContactEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContactRepository extends CrudRepository<ContactEntity, Integer> {

    @Transactional(readOnly = true)
    List<ContactEntity> findByDeveloperId(int developerId);
}
