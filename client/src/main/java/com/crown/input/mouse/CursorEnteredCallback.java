package com.crown.input.mouse;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CursorEnteredCallback {
    void onCursorEntered(@NotNull Window window, boolean entered);
}
