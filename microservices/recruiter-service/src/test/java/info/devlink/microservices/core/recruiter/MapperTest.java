package info.devlink.microservices.core.recruiter;


import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.microservices.core.recruiter.domain.RecruiterEntity;
import info.devlink.microservices.core.recruiter.service.RecruiterMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTest {
    private RecruiterMapper mapper = Mappers.getMapper(RecruiterMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Recruiter api = new Recruiter(1, 2, "Allyson Penzinski", "Job Spring Partners", "C", "adr");

        RecruiterEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDeveloperId(), entity.getDeveloperId());
        assertEquals(api.getRecruiterId(), entity.getRecruiterId());
        assertEquals(api.getRecruiterName(), entity.getRecruiterName());
        assertEquals(api.getRecruitingCompany(), entity.getRecruitingCompany());
        assertEquals(api.getCompanyRating(), entity.getCompanyRating());

        Recruiter api2 = mapper.entityToApi(entity);

        assertEquals(api.getDeveloperId(), api2.getDeveloperId());
        assertEquals(api.getRecruiterId(), api2.getRecruiterId());
        assertEquals(api.getRecruiterName(), api2.getRecruiterName());
        assertEquals(api.getRecruitingCompany(), api2.getRecruitingCompany());
        assertEquals(api.getCompanyRating(), api2.getCompanyRating());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Recruiter api = new Recruiter(1, 2, "Samatha Daniels", "La Salle Network", "3", "adr");
        List<Recruiter> apiList = Collections.singletonList(api);

        List<RecruiterEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        RecruiterEntity entity = entityList.get(0);

        assertEquals(api.getDeveloperId(), entity.getDeveloperId());
        assertEquals(api.getRecruiterId(), entity.getRecruiterId());
        assertEquals(api.getRecruiterName(), entity.getRecruiterName());
        assertEquals(api.getRecruitingCompany(), entity.getRecruitingCompany());
        assertEquals(api.getCompanyRating(), entity.getCompanyRating());

        List<Recruiter> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Recruiter api2 = api2List.get(0);

        assertEquals(api.getDeveloperId(), api2.getDeveloperId());
        assertEquals(api.getRecruiterId(), api2.getRecruiterId());
        assertEquals(api.getRecruiterName(), api2.getRecruiterName());
        assertEquals(api.getRecruitingCompany(), api2.getRecruitingCompany());
        assertEquals(api.getCompanyRating(), api2.getCompanyRating());
        assertNull(api2.getServiceAddress());
    }

}
