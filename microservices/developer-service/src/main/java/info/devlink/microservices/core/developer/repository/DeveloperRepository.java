package info.devlink.microservices.core.developer.repository;

import info.devlink.microservices.core.developer.domain.DeveloperEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DeveloperRepository extends PagingAndSortingRepository<DeveloperEntity, String > {
    Optional<DeveloperEntity> findByDeveloperId(int developerId);
}
