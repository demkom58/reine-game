package com.crown.input.keyboard;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class Keyboard {
    private final Window window;

    public Keyboard(@NotNull final Window window) {
        this.window = window;
    }

    public void setCharModsCallback(@Nullable final CharModsCallback callback) {
        GLFW.glfwSetCharModsCallback(window.getHandle(), callback == null
                ? null
                : (handle, codepoint, mods) -> callback.onCharMods(this.window, codepoint, mods));
    }

    public void setKeyCallback(@Nullable final KeyCallback callback) {
        GLFW.glfwSetKeyCallback(window.getHandle(), callback == null
                ? null
                : (handle, key, scancode, action, mods) -> callback.onKey(this.window, key, scancode, action, mods));
    }

    public void setCharCallback(@Nullable final CharCallback callback) {
        GLFW.glfwSetCharCallback(window.getHandle(), callback == null
                ? null
                : (handle, codepoint) -> callback.onChar(this.window, codepoint));
    }

    public boolean isKeyDown(int keyCode) {
        return GLFW.glfwGetKey(window.getHandle(), keyCode) == GLFW.GLFW_PRESS;
    }

    @Nullable
    public static String getKeycodeName(int integer) {
        return GLFW.glfwGetKeyName(integer, -1);
    }

    @Nullable
    public static String getScancodeName(int scancode) {
        return GLFW.glfwGetKeyName(-1, scancode);
    }

    @Nullable
    public static String getKeyName(int keycode, int scancode) {
        return GLFW.glfwGetKeyName(keycode, scancode);
    }

}
