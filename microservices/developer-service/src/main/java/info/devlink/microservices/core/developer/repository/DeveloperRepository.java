package info.devlink.microservices.core.developer.repository;

import info.devlink.microservices.core.developer.domain.Developer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DeveloperRepository extends PagingAndSortingRepository<Developer, String > {
    Optional<Developer> findByDeveloperId(int developerId);
}
