package com.crown.graphic.gl;

public class OpenGlException extends RuntimeException {
    public OpenGlException() {
    }

    public OpenGlException(String message) {
        super(message);
    }

    public OpenGlException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenGlException(Throwable cause) {
        super(cause);
    }

    public OpenGlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
