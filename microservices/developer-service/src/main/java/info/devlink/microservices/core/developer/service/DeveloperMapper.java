package info.devlink.microservices.core.developer.service;

import info.devlink.api.core.developer.Developer;
import info.devlink.microservices.core.developer.domain.DeveloperEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DeveloperMapper {
    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Developer entityToApi(DeveloperEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    DeveloperEntity apiToEntity(Developer api);
}
