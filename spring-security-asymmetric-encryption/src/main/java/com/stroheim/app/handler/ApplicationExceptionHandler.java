package com.stroheim.app.handler;

import com.stroheim.app.exception.BusinessException;
import com.stroheim.app.exception.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static com.stroheim.app.exception.ErrorCode.ERR_USER_DISABLED;
import static com.stroheim.app.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleException(final BusinessException ex) {

        final ErrorResponse body = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();

        log.info("Business exception occurred: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus() != null ? ex.getErrorCode().getHttpStatus() : BAD_REQUEST)
                .body(body);

    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleException(final DisabledException ex) {

        final ErrorResponse body = ErrorResponse.builder()
                .code(ERR_USER_DISABLED.getCode())
                .message(ERR_USER_DISABLED.getDefaultMessage())
                .build();

        log.info("Disabled exception occurred: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);

        return ResponseEntity
                .status(ERR_USER_DISABLED.getHttpStatus())
                .body(body);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(final BadCredentialsException ex) {

        final ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.BAD_CREDENTIALS.getCode())
                .message(ErrorCode.BAD_CREDENTIALS.getDefaultMessage())
                .build();

        log.info("Bad credentials exception occurred: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);

        return ResponseEntity
                .status(ErrorCode.INVALID_CURRENT_PASSWORD.getHttpStatus())
                .body(response);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(final UsernameNotFoundException ex) {

        final ErrorResponse response = ErrorResponse.builder()
                .code(ErrorCode.USERNAME_NOT_FOUND.getCode())
                .message(ErrorCode.USERNAME_NOT_FOUND.getDefaultMessage())
                .build();

        log.info("Username not found exception occurred: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);

        return new ResponseEntity<>(response, NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(INTERNAL_SERVER_ERROR.getCode())
                .message(INTERNAL_SERVER_ERROR.getDefaultMessage())
                .build();

        log.error("Unexpected exception occurred: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(final EntityNotFoundException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(ErrorCode.USER_NOT_FOUND.getCode())
                .message(ErrorCode.USER_NOT_FOUND.getDefaultMessage())
                .build();

        log.info("Entity not found exception occurred: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(final MethodArgumentNotValidException ex) {
        final List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    final String fieldName =  ((FieldError) error).getField();
                    final String errorCode = error.getCode();
                    final String errorMessage = error.getDefaultMessage();
                    errors.add(ErrorResponse.ValidationError.builder()
                            .field(fieldName)
                            .code(errorCode)
                            .message(errorCode)
                            .build());
                });

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .validationErrors(errors)
                .build();

        return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(final AuthorizationDeniedException exception) {
        log.debug(exception.getMessage(), exception);
        final ErrorResponse response = ErrorResponse.builder()
                .message("You are not authorized to perform this operation")
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .build();
        return new ResponseEntity<>(response, UNAUTHORIZED);
    }

}
