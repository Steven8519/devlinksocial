package info.devlink.microservices.core.developer.service;

import info.devlink.api.core.developer.Developer;
import info.devlink.api.core.developer.DeveloperService;
import info.devlink.microservices.core.developer.domain.DeveloperEntity;
import info.devlink.microservices.core.developer.repository.DeveloperRepository;
import info.devlink.util.exceptions.InvalidInputException;
import info.devlink.util.exceptions.NotFoundException;
import info.devlink.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

@RestController
public class DeveloperServiceImpl implements DeveloperService {

    private static final Logger LOG = LoggerFactory.getLogger(DeveloperServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final DeveloperRepository repository;

    private final DeveloperMapper mapper;

    @Autowired
    public DeveloperServiceImpl(DeveloperRepository repository, DeveloperMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Developer createDeveloper(Developer body) {

        if (body.getDeveloperId() < 1) throw new InvalidInputException("Invalid developerId: " + body.getDeveloperId());

        DeveloperEntity entity = mapper.apiToEntity(body);
        Mono<Developer> newEntity = repository.save(entity)
                .log()
                .onErrorMap(
                        DuplicateKeyException.class,
                        ex -> new InvalidInputException("Duplicate key, Developer Id: " + body.getDeveloperId()))
                .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public Mono<Developer> getDeveloper(int developerId) {

        if (developerId < 1) throw new InvalidInputException("Invalid developerId: " + developerId);

        return repository.findByDeveloperId(developerId)
                .switchIfEmpty(error(new NotFoundException("No developer found for developerId: " + developerId)))
                .log()
                .map(e -> mapper.entityToApi(e))
                .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteDeveloper(int developerId) {

        if (developerId < 1) throw new InvalidInputException("Invalid developerId: " + developerId);

        LOG.debug("deleteDeveloper: tries to delete an entity with developerId: {}", developerId);
        repository.findByDeveloperId(developerId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();
    }

}
