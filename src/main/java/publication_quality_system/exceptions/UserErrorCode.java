package publication_quality_system.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import publication_quality_system.base.BaseErrorCode;

@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("Email already exists", HttpStatus.CONFLICT),
    VALIDATION_ERROR("Validation error", HttpStatus.BAD_REQUEST);

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
