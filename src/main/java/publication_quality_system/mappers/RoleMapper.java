package publication_quality_system.mappers;

import org.mapstruct.Mapper;
import publication_quality_system.dtos.RoleDto;
import publication_quality_system.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toRoleDto(Role role);

    Role toRoleEntity(RoleDto dto);
}
