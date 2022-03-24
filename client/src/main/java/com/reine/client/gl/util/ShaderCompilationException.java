package com.reine.client.gl.util;

public class ShaderCompilationException extends RuntimeException {
    public ShaderCompilationException() {
    }

    public ShaderCompilationException(String message) {
        super(message);
    }

    public ShaderCompilationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShaderCompilationException(Throwable cause) {
        super(cause);
    }

    public ShaderCompilationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
