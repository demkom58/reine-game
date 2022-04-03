package com.crown.input.keyboard;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CharModsCallback {
    /**
     * Will be called when a Unicode character is input regardless of what modifier keys are used.
     *
     * @param window    the window that received the event
     * @param codepoint the Unicode code point of the character
     * @param mods      bitfield describing which modifier keys were held down
     */
    void onCharMods(@NotNull Window window, int codepoint, int mods);
}
