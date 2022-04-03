package com.crown.input.keyboard;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@FunctionalInterface
public interface KeyCallback {
    /**
     * Will be called when a key is pressed, repeated or released.
     *
     * @param window   the window that received the event
     * @param key      the keyboard key that was pressed or released
     * @param scancode the system-specific scancode of the key
     * @param action   the key action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td><td>{@link GLFW#GLFW_REPEAT REPEAT}</td></tr></table>
     * @param mods     bitfield describing which modifiers keys were held down
     */
    void onKey(@NotNull Window window, int key, int scancode, int action, int mods);
}
