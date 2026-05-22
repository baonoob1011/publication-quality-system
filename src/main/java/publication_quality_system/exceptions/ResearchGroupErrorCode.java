package publication_quality_system.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import publication_quality_system.base.BaseErrorCode;

@RequiredArgsConstructor
public enum ResearchGroupErrorCode implements BaseErrorCode {
    RESEARCH_GROUP_NOT_FOUND("Research group not found", HttpStatus.NOT_FOUND),
    RESEARCH_GROUP_NAME_ALREADY_EXISTS("Research group name already exists", HttpStatus.CONFLICT),
    GROUP_MEMBER_ALREADY_EXISTS("Group member already exists", HttpStatus.CONFLICT),
    GROUP_MEMBER_NOT_FOUND("Group member not found", HttpStatus.NOT_FOUND),
    GROUP_LEADER_REQUIRED("Group leader is required", HttpStatus.BAD_REQUEST),
    GROUP_LEADER_ALREADY_EXISTS("Group leader already exists", HttpStatus.CONFLICT),
    CANNOT_REMOVE_ONLY_LEADER("Cannot remove the only group leader", HttpStatus.BAD_REQUEST),
    GROUP_NOT_FOUND("Research group not found", HttpStatus.NOT_FOUND),
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
