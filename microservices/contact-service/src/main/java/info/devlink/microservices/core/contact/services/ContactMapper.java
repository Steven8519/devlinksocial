package info.devlink.microservices.core.contact.services;

import info.devlink.api.core.contact.Contact;
import info.devlink.microservices.core.contact.domain.ContactEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Contact entityToApi(ContactEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ContactEntity apiToEntity(Contact api);

    List<Contact> entityListToApiList(List<ContactEntity> entity);
    List<ContactEntity> apiListToEntityList(List<Contact> api);
}
