package publication_quality_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.dtos.UserRoleDto;
import publication_quality_system.entities.Permission;
import publication_quality_system.entities.Role;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role role);

    UserRoleDto toUserRoleDto(Role role);

    List<RoleDto> toDtoList(List<Role> roles);

    List<UserRoleDto> toUserRoleDtoList(List<Role> roles);

    @Mapping(target = "permissions", ignore = true)
    Role toEntity(RoleDto dto);

    @Mapping(target = "permissions", ignore = true)
    void updateRoleFromDto(RoleDto dto, @MappingTarget Role role);

    default Set<String> mapPermissionsToNames(Set<Permission> permissions) {
        if (permissions == null) {
            return Set.of();
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }
}
