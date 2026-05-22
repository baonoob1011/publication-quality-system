package publication_quality_system.services;

import publication_quality_system.dtos.AuthResponseDto;
import publication_quality_system.dtos.LoginRequestDto;
import publication_quality_system.dtos.LogoutRequestDto;
import publication_quality_system.dtos.RefreshTokenRequestDto;
import publication_quality_system.dtos.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRequestDto request);

    AuthResponseDto refreshToken(RefreshTokenRequestDto request);

    void logout(LogoutRequestDto request);
}
