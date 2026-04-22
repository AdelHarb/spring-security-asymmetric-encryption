package com.stroheim.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "User note found", HttpStatus.NOT_FOUND),
    CHANGE_PASSWORD_MISMATCH("CHANGE_PASSWORD_MISMATCH", "New password and confirm new password do not match", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "The current password is incorrect", HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "The account is already deactivated", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "Email already exists", HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS("PHONE_ALREADY_EXISTS", "Phone number already exists", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "Password and confirm password do not match", HttpStatus.BAD_REQUEST),
    ERR_USER_DISABLED("ERR_USER_DISABLED", "User account is disabled", HttpStatus.UNAUTHORIZED),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Invalid username or password", HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Username not found", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An internal server error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    CATEGORY_ALREADY_EXISTS_FOR_USER("CATEGORY_ALREADY_EXISTS_FOR_USER", "Category with the same name already exists for the user", HttpStatus.CONFLICT),
    UNAUTHORIZED("UNAUTHORIZED", "You are not authorized to perform this action", HttpStatus.UNAUTHORIZED)


    ;


    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;

    ErrorCode(final String code, final String defaultMessage, final HttpStatus httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
}
