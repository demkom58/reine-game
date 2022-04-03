package com.crown.input.mouse;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CursorPositionCallback {
    void onCursorPosition(@NotNull Window window, double x, double y);
}
