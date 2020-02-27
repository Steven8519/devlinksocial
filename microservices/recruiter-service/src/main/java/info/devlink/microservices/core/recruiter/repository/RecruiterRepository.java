package info.devlink.microservices.core.recruiter.repository;

import info.devlink.microservices.core.recruiter.domain.RecruiterEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RecruiterRepository extends CrudRepository<RecruiterEntity, String > {
    List<RecruiterEntity> findByDeveloperId(int developerId);
}
