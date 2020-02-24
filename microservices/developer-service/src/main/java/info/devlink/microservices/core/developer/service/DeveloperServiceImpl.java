package info.devlink.microservices.core.developer.service;

import info.devlink.api.core.developer.Developer;
import info.devlink.api.core.developer.DeveloperService;
import info.devlink.util.exceptions.InvalidInputException;
import info.devlink.util.exceptions.NotFoundException;
import info.devlink.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeveloperServiceImpl implements DeveloperService {
    private static final Logger LOG = LoggerFactory.getLogger(DeveloperServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public DeveloperServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Developer getDeveloper(int developerId) {
        LOG.debug("/developer return the found developer for developerId={}", developerId);
        if (developerId < 1) throw new InvalidInputException("Invalid developerId: " + developerId);

        if (developerId == 13) throw new NotFoundException("No developer found for developerId: " + developerId);

        return new Developer(developerId, "Sam", "Thomas", "Software Engineer", serviceUtil.getServiceAddress());
    }
}
