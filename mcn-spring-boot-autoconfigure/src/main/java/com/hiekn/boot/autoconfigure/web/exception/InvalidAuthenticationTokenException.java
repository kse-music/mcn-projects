package com.hiekn.boot.autoconfigure.web.exception;

import com.hiekn.boot.autoconfigure.base.exception.ExceptionKeys;
import org.springframework.security.core.AuthenticationException;

public class InvalidAuthenticationTokenException extends AuthenticationException {

    public InvalidAuthenticationTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidAuthenticationTokenException newInstance(Throwable cause){
        return new InvalidAuthenticationTokenException(ExceptionKeys.AUTHENTICATION_ERROR+"",cause);
    }

}
