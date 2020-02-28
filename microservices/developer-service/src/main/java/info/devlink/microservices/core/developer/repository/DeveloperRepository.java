package info.devlink.microservices.core.developer.repository;

import info.devlink.microservices.core.developer.domain.DeveloperEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface DeveloperRepository extends ReactiveCrudRepository<DeveloperEntity, String > {
    Mono<DeveloperEntity> findByDeveloperId(int developerId);
}
