package publication_quality_system.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import publication_quality_system.base.BaseErrorCode;

@RequiredArgsConstructor
public enum UploadFileErrorCode implements BaseErrorCode {
        UPLOAD_FILE_ERROR_CODE("Upload file to S3 failed", HttpStatus.NOT_FOUND),
    DELETE_FILE_FAILED("Delete file from S3 failed", HttpStatus.BAD_REQUEST);

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
