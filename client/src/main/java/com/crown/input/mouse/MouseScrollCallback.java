package com.crown.input.mouse;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MouseScrollCallback {
    void onScroll(@NotNull Window window, double xOffset, double yOffset);
}
