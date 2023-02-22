package com.cpirh.authority.exception;

import lombok.Data;

/**
 * @author ronghui
 * @Description
 * @date 2023/2/17 17:34
 */
@Data
public class AuthException extends RuntimeException {
    Integer code;
    String message;
    Object data;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(int code, String message) {
        super(message);
        this.code = code;
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
