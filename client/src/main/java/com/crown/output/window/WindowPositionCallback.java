package com.crown.output.window;

import org.jetbrains.annotations.NotNull;

public interface WindowPositionCallback {
    void onPositionChanged(@NotNull Window window, int x, int y);
}
