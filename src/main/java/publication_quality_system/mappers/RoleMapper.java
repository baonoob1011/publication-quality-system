package publication_quality_system.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.entities.Role;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "permissions", expression = "java(role.getPermissions() == null ? null : role.getPermissions().stream().map(permission -> permission.getName()).collect(java.util.stream.Collectors.toSet()))")
    RoleDto toRoleDto(Role role);

    @Mapping(target = "permissions", ignore = true)
    Role toRoleEntity(RoleDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateRoleFromDto(RoleDto dto, @MappingTarget Role entity);
}
