package publication_quality_system.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import publication_quality_system.dtos.UserDto;
import publication_quality_system.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUserEntity(UserDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateUserFromDto(UserDto dto, @MappingTarget User user);
}
