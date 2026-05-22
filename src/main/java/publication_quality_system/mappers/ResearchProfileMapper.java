package publication_quality_system.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import publication_quality_system.dtos.ResearchProfileDto;
import publication_quality_system.entities.ResearchProfile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResearchProfileMapper {

    @Mapping(source = "user.id", target = "userId")
    ResearchProfileDto toProfileDto(ResearchProfile profile);

    @Mapping(source = "userId", target = "user.id")
    ResearchProfile toProfileEntity(ResearchProfileDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateProfileFromDto(ResearchProfileDto dto, @MappingTarget ResearchProfile profile);
}
