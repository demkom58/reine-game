package com.crown.graphic.gl;

import com.reine.util.OperatingSystem;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;

public final class GraphicsLibrary {
    private static boolean reversZDepth = false;
    private static final GLFWErrorCallback ERROR_CALLBACK = GLFWErrorCallback.create(GraphicsLibrary::printError);

    public static final int OPENGL_TARGET_VERSION_MAJOR = 4;
    public static final int OPENGL_TARGET_VERSION_MINOR = 5;

    public static void init() {
        setLogOnError();

        if (!glfwInit()) {
            throw new OpenGlHostException("Failed to initialize GLFW");
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

    public static boolean isExtensionSupported(String name) {
        return glfwExtensionSupported(name);
    }

    public static Set<String> isNotSupportedExtensions(String... names) {
        final Set<String> notSupported = new HashSet<>();

        for (int i = 0; i < names.length; i++) {
            if (!isExtensionSupported(names[i])) {
                notSupported.add(names[i]);
            }
        }

        return notSupported;
    }

    public static void enableMultiSampling() {
        glEnable(GL_MULTISAMPLE);
    }

    public static void reversZDepth() {
        glDepthFunc(GL_GEQUAL);
        glClipControl(GL_LOWER_LEFT, GL_ZERO_TO_ONE);
        glClearDepth(0.0);
        reversZDepth = true;
    }

    public static boolean isReversZDepth() {
        return reversZDepth;
    }

    public static void setThrowOnError() {
        glfwSetErrorCallback(GraphicsLibrary::throwError);
    }

    public static void throwError(int errorCode, long descriptionPointer) {
        throw new OpenGlDeviceException("GLFW error occurred " + errorCode + ": " + MemoryUtil.memUTF8(descriptionPointer));
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
