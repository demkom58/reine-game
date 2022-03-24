package com.reine.client.gl.shader;

import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ShaderProgram {
    private final int handle;

    public ShaderProgram(Shader... shaders) {
        this.handle = glCreateProgram();

        for (int i = 0; i < shaders.length; i++) {
            glAttachShader(handle, shaders[i].getHandle());
        }

        glLinkProgram(handle);

        checkCompilation();
    }

    public ShaderProgram(Shader vertex, Shader frag) {
        this.handle = glCreateProgram();
        glAttachShader(handle, vertex.getHandle());
        glAttachShader(handle, frag.getHandle());
        glLinkProgram(handle);

        checkCompilation();
    }

    private void checkCompilation() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer status = stack.mallocInt(1);

            glGetProgramiv(handle, GL_LINK_STATUS, status);
            if (status.get() == GL_FALSE) {
                String log = glGetProgramInfoLog(handle);
                System.err.println("Failed to compile shader program! Log: " + log);
            }
        }
    }

    public int getHandle() {
        return handle;
    }

    public void use() {
        glUseProgram(handle);
    }

    public void delete() {
        glDeleteProgram(handle);
    }
}
