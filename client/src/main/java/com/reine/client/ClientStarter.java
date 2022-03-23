package com.reine.client;

import com.reine.util.OperatingSystem;

import static org.lwjgl.glfw.GLFW.*;

public class ClientStarter {
    public static void main(String[] args) {
        glfwInit();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        if (OperatingSystem.current() == OperatingSystem.OSX) {
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        }

        long window = glfwCreateWindow(400, 300, "Reine", 0, 0);
        if (window == 0) {
            System.err.println("Failed to create GLFW window!");
            glfwTerminate();
            return;
        }

        glfwMakeContextCurrent(window);
    }
}
