package info.devlink.microservices.core.recruiter.repository;

import info.devlink.microservices.core.recruiter.domain.RecruiterEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RecruiterRepository extends ReactiveMongoRepository<RecruiterEntity, String > {
    Flux<RecruiterEntity> findByDeveloperId(int developerId);
}
