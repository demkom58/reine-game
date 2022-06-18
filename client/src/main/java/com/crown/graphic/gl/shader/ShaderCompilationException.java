package com.crown.graphic.gl.shader;

import com.crown.graphic.gl.OpenGlHostException;

public class ShaderCompilationException extends OpenGlHostException {
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
