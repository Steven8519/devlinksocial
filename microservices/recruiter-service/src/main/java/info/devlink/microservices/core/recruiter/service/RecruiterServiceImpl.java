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

import java.util.List;

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
        try {
            RecruiterEntity entity = mapper.apiToEntity(body);
            RecruiterEntity newEntity = repository.save(entity);

            LOG.debug("createRecruiter: created a recruiter entity: {}/{}", body.getDeveloperId(), body.getRecruiterId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Developer Id: " + body.getDeveloperId() + ", Recruiter Id:" + body.getRecruiterId());
        }
    }

    @Override
    public List<Recruiter> getRecruiters(int developerId) {

        if (developerId < 1) throw new InvalidInputException("Invalid developerId: " + developerId);

        List<RecruiterEntity> entityList = repository.findByDeveloperId(developerId);
        List<Recruiter> list = mapper.entityListToApiList(entityList);
        list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getRecruiters: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteRecruiters(int developerId) {
        LOG.debug("deleteRecruiters: tries to delete recruiters for the developer with developerId: {}", developerId);
        repository.deleteAll(repository.findByDeveloperId(developerId));
    }
}
