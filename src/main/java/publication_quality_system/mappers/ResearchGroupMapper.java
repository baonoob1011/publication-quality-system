package publication_quality_system.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.entities.ResearchGroup;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResearchGroupMapper {

    @Mapping(source = "leader.id", target = "leaderId")
    ResearchGroupDto toGroupDto(ResearchGroup group);

    @Mapping(source = "leaderId", target = "leader.id")
    ResearchGroup toGroupEntity(ResearchGroupDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    void updateGroupFromDto(ResearchGroupDto dto, @MappingTarget ResearchGroup group);
}
