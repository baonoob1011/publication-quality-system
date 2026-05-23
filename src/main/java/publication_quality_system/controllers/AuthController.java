package publication_quality_system.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import publication_quality_system.base.BaseController;
import publication_quality_system.base.BaseResponse;
import publication_quality_system.dtos.AuthResponseDto;
import publication_quality_system.dtos.CurrentUserDto;
import publication_quality_system.dtos.LoginRequestDto;
import publication_quality_system.dtos.LogoutRequestDto;
import publication_quality_system.dtos.RefreshTokenRequestDto;
import publication_quality_system.dtos.RegisterRequestDto;
import publication_quality_system.security.CurrentUser;
import publication_quality_system.services.AuthService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<AuthResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        return created(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        return success(authService.login(request), "Login successfully");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<AuthResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        return success(authService.refreshToken(request), "Token refreshed successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@Valid @RequestBody LogoutRequestDto request) {
        authService.logout(request);
        return success(null, "Logout successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<BaseResponse<CurrentUserDto>> me(Authentication authentication) {
        CurrentUserDto dto = new CurrentUserDto();
        if (authentication.getDetails() instanceof CurrentUser currentUser) {
            dto.setFullName(currentUser.getFullName());
            dto.setEmail(currentUser.getEmail());
            dto.setSub(currentUser.getSub());
        } else {
            dto.setEmail(authentication.getName());
        }

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        dto.setAuthorities(authorities);
        return success(dto, "Current user retrieved successfully");
    }
}
