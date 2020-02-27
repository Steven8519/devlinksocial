package info.devlink.microservices.core.recruiter.repository;

import info.devlink.microservices.core.recruiter.domain.Recruiter;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RecruiterRepository extends CrudRepository<Recruiter, String > {
    List<Recruiter> findByDeveloperId(int developerId);
}
