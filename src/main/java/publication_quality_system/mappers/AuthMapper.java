package publication_quality_system.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import publication_quality_system.dtos.AuthResponseDto;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "accessToken", expression = "java(result.accessToken())")
    @Mapping(target = "refreshToken", expression = "java(result.refreshToken())")
    @Mapping(target = "idToken", expression = "java(result.idToken())")
    @Mapping(target = "tokenType", expression = "java(result.tokenType())")
    @Mapping(target = "expiresIn", expression = "java(result.expiresIn())")
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "email", ignore = true)
    AuthResponseDto toAuthResponse(AuthenticationResultType result);
}
