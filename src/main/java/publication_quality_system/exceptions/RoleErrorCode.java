package publication_quality_system.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import publication_quality_system.base.BaseErrorCode;

@RequiredArgsConstructor
public enum RoleErrorCode implements BaseErrorCode {
    ROLE_NOT_FOUND("Role not found", HttpStatus.NOT_FOUND),
    ROLE_ALREADY_EXISTS("Role already exists", HttpStatus.CONFLICT),
    ROLE_NAME_REQUIRED("Role name is required", HttpStatus.BAD_REQUEST),
    ROLE_IN_USE("Role is in use", HttpStatus.CONFLICT),
    COGNITO_GROUP_SYNC_FAILED("Cognito group sync failed", HttpStatus.BAD_GATEWAY),
    COGNITO_GROUP_NOT_FOUND("Cognito group not found", HttpStatus.NOT_FOUND),
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
