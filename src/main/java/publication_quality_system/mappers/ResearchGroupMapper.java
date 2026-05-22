package publication_quality_system.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import publication_quality_system.dtos.ResearchGroupDto;
import publication_quality_system.entities.ResearchGroup;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResearchGroupMapper {

    @Mapping(target = "memberCount", ignore = true)
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "members", ignore = true)
    ResearchGroupDto toGroupDto(ResearchGroup group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    @BeanMapping(builder = @Builder(disableBuilder = true))
    ResearchGroup toGroupEntity(ResearchGroupDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    void updateGroupFromDto(ResearchGroupDto dto, @MappingTarget ResearchGroup group);
}
