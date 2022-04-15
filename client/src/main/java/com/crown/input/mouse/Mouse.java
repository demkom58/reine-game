package com.crown.input.mouse;

import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class Mouse {
    private CursorPositionCallback positionCallback;
    private CursorEnteredCallback enteredCallback;

    private final Window window;

    private double x;
    private double y;

    public double deltaX;
    public double deltaY;

    private boolean entered;
    private boolean grabbed = false;

    public Mouse(@NotNull final Window window) {
        this.window = window;
        GLFW.glfwSetCursorEnterCallback(window.getHandle(), this::onEntered);
        GLFW.glfwSetCursorPosCallback(window.getHandle(), this::onMoved);
    }

    private void onMovedWithCallback(long window, double x, double y) {
        this.onMoved(window, x, y);
        this.positionCallback.onCursorPosition(this.window, x, y);
    }

    private void onMoved(long window, double x, double y) {
        Dimension windowSize = this.window.getSize();
        y = windowSize.getHeight() - y;

        this.deltaX = x - this.x;
        this.deltaY = y - this.y;

        this.x = x;
        this.y = y;

        if (grabbed) {
            setCursorInCenter();
        }
    }

    private void onEnteredWithCallback(long window, boolean entered) {
        this.onEntered(window, entered);
        this.enteredCallback.onCursorEntered(this.window, entered);
    }

    private void onEntered(long window, boolean entered) {
        this.entered = entered;
    }

    public void grabMouseCursor() {
        GLFW.glfwSetInputMode(window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        this.deltaX = 0;
        this.deltaY = 0;
        this.grabbed = true;
    }

    public void ungrabMouseCursor() {
        final Dimension size = window.getSize();
        setCursorPosition(size.getWidth() / 2d, size.getHeight() / 2d);
        GLFW.glfwSetInputMode(window.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        this.grabbed = false;
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void setCursorInCenter() {
        final Dimension size = window.getSize();
        setCursorPosition(size.getWidth() / 2D, size.getHeight() / 2D);
    }

    public void setButtonCallback(@Nullable final MouseButtonCallback callback) {
        GLFW.glfwSetMouseButtonCallback(window.getHandle(), callback == null
                ? null
                : (handle, button, action, mods) -> callback.onMouseButton(this.window, button, action, mods));
    }

    public void setScrollCallback(@Nullable final MouseScrollCallback callback) {
        GLFW.glfwSetScrollCallback(window.getHandle(), callback == null
                ? null
                : (handle, x, y) -> callback.onScroll(this.window, x, y));
    }

    public void setEnteredCallback(@Nullable final CursorEnteredCallback callback) {
        this.enteredCallback = callback;

        if (callback == null) {
            GLFW.glfwSetCursorEnterCallback(window.getHandle(), this::onEntered);
        } else {
            GLFW.glfwSetCursorEnterCallback(window.getHandle(), this::onEnteredWithCallback);
        }
    }

    public void setPositionCallback(@Nullable final CursorPositionCallback callback) {
        this.positionCallback = callback;

        if (callback == null) {
            GLFW.glfwSetCursorPosCallback(window.getHandle(), this::onMoved);
        } else {
            GLFW.glfwSetCursorPosCallback(window.getHandle(), this::onMovedWithCallback);
        }
    }

    public void setCursorPosition(double x, double y) {
        this.x = x;
        this.y = y;

        GLFW.glfwSetCursorPos(window.getHandle(), x, y);
    }

    public boolean isButtonPressed(int buttonId) {
        return GLFW.glfwGetMouseButton(window.getHandle(), buttonId) == GLFW.GLFW_PRESS;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public boolean isEntered() {
        return entered;
    }
}
