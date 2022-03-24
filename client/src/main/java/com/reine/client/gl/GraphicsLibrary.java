package com.reine.client.gl;

import com.reine.client.gl.util.GraphicsError;
import com.reine.util.OperatingSystem;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

public final class GraphicsLibrary {
    private static final GLFWErrorCallback ERROR_CALLBACK = GLFWErrorCallback.create(GraphicsLibrary::printError);

    public static final int OPENGL_TARGET_VERSION_MAJOR = 3;
    public static final int OPENGL_TARGET_VERSION_MINOR = 3;

    public static void init() {
        setLogOnError();

        if (!glfwInit()) {
            throw new GraphicsError("Failed to initialize GLFW");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, OPENGL_TARGET_VERSION_MAJOR);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, OPENGL_TARGET_VERSION_MINOR);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        if (OperatingSystem.current() == OperatingSystem.OSX) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }
    }

    public static void setLogOnError() {
        GLFWErrorCallback glfwErrorCallback = glfwSetErrorCallback(ERROR_CALLBACK);
        if (glfwErrorCallback != null) {
            glfwErrorCallback.free();
        }
    }

    public static void setThrowOnError() {
        glfwSetErrorCallback(GraphicsLibrary::throwError);
    }

    public static void throwError(int errorCode, long descriptionPointer) {
        throw new IllegalStateException("GLFW error occurred " + errorCode + ": " + MemoryUtil.memUTF8(descriptionPointer));
    }

    public static void printError(int errorCode, long descriptionPointer) {
        String description = MemoryUtil.memUTF8(descriptionPointer);
        System.out.println("************* GRAPHICS LIBRARY ERROR *************");
        System.out.println(errorCode + ": " + description);
    }

    public static void destroy() {
        ERROR_CALLBACK.close();
        glfwTerminate();
    }
}
