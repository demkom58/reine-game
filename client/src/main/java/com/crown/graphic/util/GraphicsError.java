package com.crown.graphic.util;

public class GraphicsError extends Error {
    public GraphicsError() {
    }

    public GraphicsError(String message) {
        super(message);
    }

    public GraphicsError(String message, Throwable cause) {
        super(message, cause);
    }

    public GraphicsError(Throwable cause) {
        super(cause);
    }

    public GraphicsError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
