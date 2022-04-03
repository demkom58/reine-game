package com.crown.input.keyboard;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CharCallback {
    /**
     * Will be called when a Unicode character is input.
     *
     * @param window    the window that received the event
     * @param codepoint the Unicode code point of the character
     */
    void onChar(@NotNull Window window, int codepoint);
}
