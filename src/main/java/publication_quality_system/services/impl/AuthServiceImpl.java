package publication_quality_system.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import publication_quality_system.properties.CognitoProperties;
import publication_quality_system.dtos.AuthResponseDto;
import publication_quality_system.dtos.LoginRequestDto;
import publication_quality_system.dtos.LogoutRequestDto;
import publication_quality_system.dtos.RefreshTokenRequestDto;
import publication_quality_system.dtos.RegisterRequestDto;
import publication_quality_system.entities.User;
import publication_quality_system.exceptions.AppException;
import publication_quality_system.exceptions.AuthErrorCode;
import publication_quality_system.repositories.UserRepository;
import publication_quality_system.services.AuthService;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminUpdateUserAttributesRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.GlobalSignOutRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotConfirmedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String HMAC_SHA_256 = "HmacSHA256";
    private static final String USERNAME = "USERNAME";
    private static final String PASSWORD = "PASSWORD";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String SECRET_HASH = "SECRET_HASH";

    private final CognitoIdentityProviderClient cognitoClient;
    private final CognitoProperties cognitoProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.findByUsername(request.getEmail()).isPresent()
                || userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(AuthErrorCode.USER_ALREADY_EXISTS);
        }

        try {
            SignUpRequest.Builder signUpBuilder = SignUpRequest.builder()
                    .clientId(cognitoProperties.getClientId())
                    .username(request.getEmail())
                    .password(request.getPassword())
                    .userAttributes(
                            AttributeType.builder().name("email").value(request.getEmail()).build(),
                            AttributeType.builder().name("name").value(resolveFullName(request)).build()
                    );

            if (hasClientSecret()) {
                signUpBuilder.secretHash(calculateSecretHash(request.getEmail()));
            }

            SignUpResponse signUpResponse = cognitoClient.signUp(signUpBuilder.build());
            confirmAndVerifyEmailIfNeeded(request.getEmail(), signUpResponse);
            saveLocalUser(request);

            LoginRequestDto loginRequest = new LoginRequestDto();
            loginRequest.setEmail(request.getEmail());
            loginRequest.setPassword(request.getPassword());
            return login(loginRequest);
        } catch (UsernameExistsException exception) {
            throw new AppException(AuthErrorCode.USER_ALREADY_EXISTS);
        } catch (UserNotConfirmedException exception) {
            throw new AppException(AuthErrorCode.USER_NOT_CONFIRMED);
        }
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            Map<String, String> authParameters = new HashMap<>();
            authParameters.put(USERNAME, request.getEmail());
            authParameters.put(PASSWORD, request.getPassword());
            putSecretHash(authParameters, request.getEmail());

            InitiateAuthResponse response = cognitoClient.initiateAuth(InitiateAuthRequest.builder()
                    .clientId(cognitoProperties.getClientId())
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(authParameters)
                    .build());

            AuthResponseDto authResponse = toAuthResponse(response.authenticationResult());
            authResponse.setUsername(request.getEmail());
            authResponse.setEmail(request.getEmail());
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(user -> authResponse.setEmail(user.getEmail()));
            return authResponse;
        } catch (UserNotConfirmedException exception) {
            throw new AppException(AuthErrorCode.USER_NOT_CONFIRMED);
        } catch (NotAuthorizedException exception) {
            throw new AppException(AuthErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Override
    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        try {
            Map<String, String> authParameters = new HashMap<>();
            authParameters.put(REFRESH_TOKEN, request.getRefreshToken());

            InitiateAuthResponse response = cognitoClient.initiateAuth(InitiateAuthRequest.builder()
                    .clientId(cognitoProperties.getClientId())
                    .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .authParameters(authParameters)
                    .build());

            AuthResponseDto authResponse = toAuthResponse(response.authenticationResult());
            if (authResponse.getRefreshToken() == null) {
                authResponse.setRefreshToken(request.getRefreshToken());
            }
            return authResponse;
        } catch (NotAuthorizedException exception) {
            throw new AppException(AuthErrorCode.TOKEN_REFRESH_FAILED);
        }
    }

    @Override
    public void logout(LogoutRequestDto request) {
        cognitoClient.globalSignOut(GlobalSignOutRequest.builder()
                .accessToken(request.getAccessToken())
                .build());
    }

    private void saveLocalUser(RegisterRequestDto request) {
        User user = new User();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    private void confirmAndVerifyEmailIfNeeded(String email, SignUpResponse signUpResponse) {
        if (Boolean.FALSE.equals(signUpResponse.userConfirmed())) {
            cognitoClient.adminConfirmSignUp(AdminConfirmSignUpRequest.builder()
                    .userPoolId(cognitoProperties.getUserPoolId())
                    .username(email)
                    .build());
        }

        cognitoClient.adminUpdateUserAttributes(AdminUpdateUserAttributesRequest.builder()
                .userPoolId(cognitoProperties.getUserPoolId())
                .username(email)
                .userAttributes(AttributeType.builder()
                        .name("email_verified")
                        .value("true")
                        .build())
                .build());
    }

    private AuthResponseDto toAuthResponse(AuthenticationResultType result) {
        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(result.accessToken());
        response.setRefreshToken(result.refreshToken());
        response.setIdToken(result.idToken());
        response.setTokenType(result.tokenType());
        response.setExpiresIn(result.expiresIn());
        return response;
    }

    private void putSecretHash(Map<String, String> authParameters, String username) {
        if (hasClientSecret()) {
            authParameters.put(SECRET_HASH, calculateSecretHash(username));
        }
    }

    private String calculateSecretHash(String username) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                    cognitoProperties.getClientSecret().getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA_256
            );
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal((username + cognitoProperties.getClientId()).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception exception) {
            throw new AppException(AuthErrorCode.COGNITO_ERROR, "Cannot calculate Cognito SECRET_HASH");
        }
    }

    private boolean hasClientSecret() {
        return cognitoProperties.getClientSecret() != null && !cognitoProperties.getClientSecret().isBlank();
    }

    private String resolveFullName(RegisterRequestDto request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            return request.getEmail();
        }
        return request.getFullName();
    }
}
