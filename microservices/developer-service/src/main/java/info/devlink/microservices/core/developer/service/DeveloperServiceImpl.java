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
        try {
            DeveloperEntity entity = mapper.apiToEntity(body);
            DeveloperEntity newEntity = repository.save(entity);

            LOG.debug("createDeveloper: entity created for developerId: {}", body.getDeveloperId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Developer Id: " + body.getDeveloperId());
        }
    }

    @Override
    public Developer getDeveloper(int developerId) {

        if (developerId < 1) throw new InvalidInputException("Invalid developerId: " + developerId);

        DeveloperEntity entity = repository.findByDeveloperId(developerId)
                .orElseThrow(() -> new NotFoundException("No developer found for developerId: " + developerId));

        Developer response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getDeveloper: found developerId: {}", response.getDeveloperId());

        return response;
    }

    @Override
    public void deleteDeveloper(int developerId) {
        LOG.debug("deleteDeveloper: tries to delete an entity with developerId: {}", developerId);
        repository.findByDeveloperId(developerId).ifPresent(e -> repository.delete(e));
    }
}
