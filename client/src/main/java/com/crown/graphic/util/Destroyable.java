package com.crown.graphic.util;

public interface Destroyable extends AutoCloseable {
    void destroy();

    @Override
    default void close() {
        destroy();
    }
}
