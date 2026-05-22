package publication_quality_system.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import publication_quality_system.base.BaseErrorCode;

@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    USER_ALREADY_EXISTS("User already exists", HttpStatus.CONFLICT),
    USER_NOT_CONFIRMED("User is not confirmed", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("Invalid credentials", HttpStatus.UNAUTHORIZED),
    COGNITO_ERROR("Cognito service error", HttpStatus.BAD_GATEWAY),
    TOKEN_REFRESH_FAILED("Token refresh failed", HttpStatus.UNAUTHORIZED),
    LOGOUT_FAILED("Logout failed", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
