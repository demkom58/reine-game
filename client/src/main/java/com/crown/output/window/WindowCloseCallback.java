package com.crown.output.window;

import org.jetbrains.annotations.NotNull;

public interface WindowCloseCallback {
    void onClose(@NotNull Window window);
}
