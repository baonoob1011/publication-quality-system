package publication_quality_system.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import publication_quality_system.base.BaseErrorCode;

@RequiredArgsConstructor
public enum RoleErrorCode implements BaseErrorCode {
    ROLE_NOT_FOUND("Role not found", HttpStatus.NOT_FOUND),
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
