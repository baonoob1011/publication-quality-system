package publication_quality_system.base;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    default int getCode() {
        return getHttpStatus().value();
    }

    String getMessage();

    HttpStatus getHttpStatus();
}