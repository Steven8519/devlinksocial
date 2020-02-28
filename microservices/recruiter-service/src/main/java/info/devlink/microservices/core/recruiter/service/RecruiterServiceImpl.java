package info.devlink.microservices.core.recruiter.service;

import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.api.core.recruiter.RecruiterService;
import info.devlink.microservices.core.recruiter.domain.RecruiterEntity;
import info.devlink.microservices.core.recruiter.repository.RecruiterRepository;
import info.devlink.util.exceptions.InvalidInputException;
import info.devlink.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RecruiterServiceImpl implements RecruiterService {

    private static final Logger LOG = LoggerFactory.getLogger(RecruiterServiceImpl.class);

    private final RecruiterRepository repository;

    private final RecruiterMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public RecruiterServiceImpl(RecruiterRepository repository, RecruiterMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }


    @Override
    public Recruiter createRecruiter(Recruiter body) {
        if (body.getDeveloperId() < 1) throw new InvalidInputException("Invalid developerId: " + body.getDeveloperId());

        RecruiterEntity entity = mapper.apiToEntity(body);
        Mono<Recruiter> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Developer Id: " + body.getDeveloperId() + ", Recruiter Id:" + body.getRecruiterId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public Flux<Recruiter> getRecruiters(int developerId) {
        if (developerId < 1) throw new InvalidInputException("Invalid developerId: " + developerId);

        return repository.findByDeveloperId(developerId)
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteRecruiters(int developerId) {
        if (developerId < 1) throw new InvalidInputException("Invalid developerId: " + developerId);

        LOG.debug("deleteRecruiters: tries to delete recruiters for the developer with developerId: {}", developerId);
        repository.deleteAll(repository.findByDeveloperId(developerId)).block();
    }
}
