package info.devlink.microservices.core.developer;

import info.devlink.api.core.developer.Developer;
import info.devlink.microservices.core.developer.domain.DeveloperEntity;
import info.devlink.microservices.core.developer.service.DeveloperMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.*;

public class MapperTest {
    private DeveloperMapper mapper = Mappers.getMapper(DeveloperMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Developer api = new Developer(1, "Sam", "Smith", "Python Developer", "sa");

        DeveloperEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDeveloperId(), entity.getDeveloperId());
        assertEquals(api.getFirstName(), entity.getFirstName());
        assertEquals(api.getLastName(), entity.getLastName());
        assertEquals(api.getDeveloperType(), entity.getDeveloperType());

        Developer api2 = mapper.entityToApi(entity);

        assertEquals(api.getDeveloperId(), api2.getDeveloperId());
        assertEquals(api.getFirstName(), api2.getFirstName());
        assertEquals(api.getLastName(),      api2.getLastName());
        assertEquals(api.getDeveloperType(),    api2.getDeveloperType());
        assertNull(api2.getServiceAddress());
    }
}
