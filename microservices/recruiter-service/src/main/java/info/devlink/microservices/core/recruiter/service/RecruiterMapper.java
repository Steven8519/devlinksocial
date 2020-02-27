package info.devlink.microservices.core.recruiter.service;

import info.devlink.api.core.recruiter.Recruiter;
import info.devlink.microservices.core.recruiter.domain.RecruiterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecruiterMapper {
    @Mappings({
            @Mapping(target = "recruiterName", source="entity.recruiterName"),
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Recruiter entityToApi(RecruiterEntity entity);

    @Mappings({
            @Mapping(target = "recruiterName", source="api.recruiterName"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    RecruiterEntity apiToEntity(Recruiter api);

    List<Recruiter> entityListToApiList(List<RecruiterEntity> entity);
    List<RecruiterEntity> apiListToEntityList(List<Recruiter> api);
}
