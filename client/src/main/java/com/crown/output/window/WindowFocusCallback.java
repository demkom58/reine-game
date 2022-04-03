package com.crown.output.window;

import org.jetbrains.annotations.NotNull;

public interface WindowFocusCallback {
    void onFocus(@NotNull Window window, boolean focused);
}
