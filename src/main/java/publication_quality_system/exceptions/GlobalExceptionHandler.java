package publication_quality_system.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import publication_quality_system.base.BaseErrorCode;
import publication_quality_system.base.BaseResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Void>> handleAppException(AppException exception) {
        BaseErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(BaseResponse.error(errorCode.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse(UserErrorCode.VALIDATION_ERROR.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(CognitoIdentityProviderException.class)
    public ResponseEntity<BaseResponse<Void>> handleCognitoException(CognitoIdentityProviderException exception) {
        String errorCode = exception.awsErrorDetails() != null
                ? exception.awsErrorDetails().errorCode()
                : "UNKNOWN";

        String errorMessage = exception.awsErrorDetails() != null
                ? exception.awsErrorDetails().errorMessage()
                : exception.getMessage();

        int statusCode = exception.statusCode();
        log.error("Cognito error. code={}, message={}, status={}", errorCode, errorMessage, statusCode);

        String message = "Cognito error [" + errorCode + "]: " + errorMessage;
        return ResponseEntity.status(AuthErrorCode.COGNITO_ERROR.getHttpStatus())
                .body(BaseResponse.error(AuthErrorCode.COGNITO_ERROR.getCode(), message));
    }
}
