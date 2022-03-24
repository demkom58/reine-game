package com.reine.client.gl;

import com.reine.client.gl.util.GraphicsError;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;

    private String title;
    private boolean vsync;
    private boolean fullscreen;

    private Dimension windowedPosition = null;
    private Dimension windowedSize = null;

    public Window(int width, int height, String title) {
        this(width, height, title, 0, 0);
    }

    public Window(int width, int height, String title, long monitor, long share) {
        this.title = title;

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.handle = glfwCreateWindow(width, height, title, monitor, share);
        if (handle == NULL) {
            throw new GraphicsError("Failed to create GLFW window!");
        }

        glfwMakeContextCurrent(handle);
        GL.createCapabilities();
    }

    public long getHandle() {
        return handle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(handle, title);
    }

    public boolean isVsync() {
        return vsync;
    }

    public void setVsync(boolean vsync) {
        this.vsync = vsync;
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public boolean isFocused() {
        return glfwGetWindowAttrib(handle, GLFW_FOCUSED) == GLFW_TRUE;
    }

    public void focus() {
        glfwFocusWindow(handle);
    }

    public boolean isIconified() {
        return glfwGetWindowAttrib(handle, GLFW_ICONIFIED) == GLFW_TRUE;
    }

    public boolean isResizable() {
        return glfwGetWindowAttrib(handle, GLFW_RESIZABLE) == GLFW_TRUE;
    }

    public void setResizable(boolean value) {
        glfwSetWindowAttrib(handle, GLFW_RESIZABLE, value ? GLFW_TRUE : GLFW_FALSE);
    }

    public boolean isVisible() {
        return glfwGetWindowAttrib(handle, GLFW_VISIBLE) == GLFW_TRUE;
    }

    public void show() {
        glfwShowWindow(handle);
    }

    public void hide() {
        glfwHideWindow(handle);
    }

    public boolean isDecorated() {
        return glfwGetWindowAttrib(handle, GLFW_DECORATED) == GLFW_TRUE;
    }

    public void setDecorated(boolean value) {
        glfwSetWindowAttrib(handle, GLFW_DECORATED, value ? GLFW_TRUE : GLFW_FALSE);
    }

    public boolean isAutoIconify() {
        return glfwGetWindowAttrib(handle, GLFW_AUTO_ICONIFY) == GLFW_TRUE;
    }

    public void setAutoIconify(boolean value) {
        glfwSetWindowAttrib(handle, GLFW_AUTO_ICONIFY, value ? GLFW_TRUE : GLFW_FALSE);
    }

    public boolean isFloating() {
        return glfwGetWindowAttrib(handle, GLFW_FLOATING) == GLFW_TRUE;
    }

    public void setFloating(boolean value) {
        glfwSetWindowAttrib(handle, GLFW_FLOATING, value ? GLFW_TRUE : GLFW_FALSE);
    }

    public boolean isMaximized() {
        return glfwGetWindowAttrib(handle, GLFW_MAXIMIZED) == GLFW_TRUE;
    }

    public void maximize() {
        glfwMaximizeWindow(handle);
    }

    public boolean isCenterCursor() {
        return glfwGetWindowAttrib(handle, GLFW_CENTER_CURSOR) == GLFW_TRUE;
    }

    public boolean isTransparent() {
        return glfwGetWindowAttrib(handle, GLFW_TRANSPARENT_FRAMEBUFFER) == GLFW_TRUE;
    }

    public boolean isFocusOnShow() {
        return glfwGetWindowAttrib(handle, GLFW_FOCUS_ON_SHOW) == GLFW_TRUE;
    }

    public void setFocusOnShow(boolean value) {
        glfwSetWindowAttrib(handle, GLFW_FOCUS_ON_SHOW, value ? GLFW_TRUE : GLFW_FALSE);
    }

    public boolean isMousePassthrough() {
        return glfwGetWindowAttrib(handle, GLFW_MOUSE_PASSTHROUGH) == GLFW_TRUE;
    }

    public void setMousePassthrough(boolean value) {
        glfwSetWindowAttrib(handle, GLFW_MOUSE_PASSTHROUGH, value ? GLFW_TRUE : GLFW_FALSE);
    }

    public boolean isScaleToMonitor() {
        return glfwGetWindowAttrib(handle, GLFW_SCALE_TO_MONITOR) == GLFW_TRUE;
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void makeCurrentContext() {
        glfwMakeContextCurrent(handle);
    }

    public void swapBuffers() {
        glfwSwapBuffers(handle);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void update() {
        glfwPollEvents();
        glfwSwapBuffers(handle);
    }

    public void setPosition(int x, int y) {
        glfwSetWindowPos(handle, x, y);
    }

    public void setPosition(Dimension position) {
        glfwSetWindowPos(handle, position.width, position.height);
    }

    public Dimension getPosition() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pX = stack.mallocInt(1);
            IntBuffer pY = stack.mallocInt(1);

            glfwGetWindowPos(handle, pX, pY);

            return new Dimension(pX.get(), pY.get());
        }
    }

    public void setSize(int width, int height) {
        glfwSetWindowSize(handle, width, height);
    }

    public void setSize(Dimension size) {
        glfwSetWindowSize(handle, size.width, size.height);
    }

    public Dimension getSize() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(handle, pWidth, pHeight);

            return new Dimension(pWidth.get(), pHeight.get());
        }
    }


    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        if (this.fullscreen == fullscreen) {
            return;
        }

        this.fullscreen = fullscreen;

        int height;
        int width;

        if (fullscreen) {
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            if (vidMode == null) {
                throw new RuntimeException("Can't get primary monitor video mode");
            }

            windowedSize = getSize();
            windowedPosition = getPosition();

            width = vidMode.width();
            height = vidMode.height();
        } else {
            width = windowedSize.width;
            height = windowedSize.height;
        }

        if (width <= 0) {
            width = 1;
        }

        if (height <= 0) {
            height = 1;
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        long newPointer = glfwCreateWindow(width, height, title, fullscreen ? glfwGetPrimaryMonitor() : MemoryUtil.NULL, handle);

        boolean wasVisible = isVisible();
        glfwDestroyWindow(handle);
        handle = newPointer;

        if (!fullscreen) {
            this.setPosition(windowedPosition);
        }

        makeCurrentContext();
        if (wasVisible) {
            show();
        }
    }

    public void moveToCenter() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(handle, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            assert vidmode != null;
            setPosition((vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
        }
    }

    public void destroy() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
    }

    public void setResizeCallback(GLFWWindowSizeCallbackI callback) {
        glfwSetWindowSizeCallback(handle, callback);
    }

    public void setPositionCallback(GLFWWindowPosCallbackI callback) {
        glfwSetWindowPosCallback(handle, callback);
    }

    public void setFocusCallback(GLFWWindowFocusCallbackI callback) {
        glfwSetWindowFocusCallback(handle, callback);
    }

    public void setCloseCallback(GLFWWindowCloseCallbackI callback) {
        glfwSetWindowCloseCallback(handle, callback);
    }
}
