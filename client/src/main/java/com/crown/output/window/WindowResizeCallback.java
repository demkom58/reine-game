package com.crown.output.window;

import org.jetbrains.annotations.NotNull;

public interface WindowResizeCallback {
    void onResize(@NotNull Window window, int width, int height);
}
