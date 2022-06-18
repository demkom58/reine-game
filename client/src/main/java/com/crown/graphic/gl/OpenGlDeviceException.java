package com.crown.graphic.gl;

public class OpenGlDeviceException extends OpenGlException {
    public OpenGlDeviceException() {
    }

    public OpenGlDeviceException(String message) {
        super(message);
    }

    public OpenGlDeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OpenGlDeviceException(Throwable cause) {
        super(cause);
    }

    public OpenGlDeviceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
