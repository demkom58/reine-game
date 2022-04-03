package com.crown.input.mouse;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MouseButtonCallback {
    void onMouseButton(@NotNull Window window, int button, int action, int mods);
}
