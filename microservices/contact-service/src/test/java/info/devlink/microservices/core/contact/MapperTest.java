package info.devlink.microservices.core.contact;

import info.devlink.api.core.contact.Contact;
import info.devlink.microservices.core.contact.domain.ContactEntity;
import info.devlink.microservices.core.contact.services.ContactMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTest {
    private ContactMapper mapper = Mappers.getMapper(ContactMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Contact api = new Contact(1, 2, 3, "sample@gmail.com", "3125554444", "addr");

        ContactEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDeveloperId(), entity.getDeveloperId());
        assertEquals(api.getContactId(), entity.getContactId());
        assertEquals(api.getContactId(), entity.getContactId());
        assertEquals(api.getEmail(), entity.getEmail());
        assertEquals(api.getPhoneNumber(), entity.getPhoneNumber());

        Contact api2 = mapper.entityToApi(entity);

        assertEquals(api.getDeveloperId(), api2.getDeveloperId());
        assertEquals(api.getContactId(), api2.getContactId());
        assertEquals(api.getContactId(), api2.getContactId());
        assertEquals(api.getEmail(), api2.getEmail());
        assertEquals(api.getPhoneNumber(), api2.getPhoneNumber());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Contact api = new Contact(1, 2, 3, "sample@gmail.com", "3125554444", "addr");
        List<Contact> apiList = Collections.singletonList(api);

        List<ContactEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        ContactEntity entity = entityList.get(0);

        assertEquals(api.getDeveloperId(), entity.getDeveloperId());
        assertEquals(api.getContactId(), entity.getContactId());
        assertEquals(api.getContactId(), entity.getContactId());
        assertEquals(api.getEmail(), entity.getEmail());
        assertEquals(api.getPhoneNumber(), entity.getPhoneNumber());

        List<Contact> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Contact api2 = api2List.get(0);

        assertEquals(api.getDeveloperId(), api2.getDeveloperId());
        assertEquals(api.getContactId(), api2.getContactId());
        assertEquals(api.getContactId(), api2.getContactId());
        assertEquals(api.getEmail(), api2.getEmail());
        assertEquals(api.getPhoneNumber(), api2.getPhoneNumber());
        assertNull(api2.getServiceAddress());
    }
}
