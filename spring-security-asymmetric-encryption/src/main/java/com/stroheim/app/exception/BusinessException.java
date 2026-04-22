package com.stroheim.app.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {


    private final ErrorCode errorCode;
    private final Object[] args;

    public BusinessException(final ErrorCode errorCode, final Object... args) {
        super(getFormatedMessage(errorCode, args));
        this.errorCode = errorCode;
        this.args = args;
    }

    private static String getFormatedMessage(ErrorCode errorCode, Object[] args) {

        if(args != null && args.length > 0) {
            return String.format(errorCode.getDefaultMessage(), args);
        }

        return errorCode.getDefaultMessage();
    }
}
