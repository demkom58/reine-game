package com.reine.client.gl.shader;

import com.google.common.io.Resources;
import com.reine.client.gl.util.ShaderCompilationException;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Shader {
    private final int handle;

    public Shader(URL resource, boolean vertex) {
        try {
            this.handle = glCreateShader(vertex ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
            glShaderSource(handle, Resources.toString(resource, StandardCharsets.UTF_8));
            glCompileShader(handle);

            try (MemoryStack stack = stackPush()) {
                IntBuffer status = stack.mallocInt(1);

                glGetShaderiv(handle, GL_COMPILE_STATUS, status);
                if (status.get() == GL_FALSE) {
                    String log = glGetShaderInfoLog(handle);
                    System.err.println("Failed to compile shader! (" + resource + ") Log: " + log);
                }
            }
        } catch (IOException e) {
            throw new ShaderCompilationException("Failed to compile shader " + resource, e);
        }
    }

    public Shader(String src, boolean vertex) {
        this.handle = glCreateShader(vertex ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
        glShaderSource(handle, src);
        glCompileShader(handle);

        try (MemoryStack stack = stackPush()) {
            IntBuffer status = stack.mallocInt(1);

            glGetShaderiv(handle, GL_COMPILE_STATUS, status);
            if (status.get() == GL_FALSE) {
                String log = glGetShaderInfoLog(handle);
                System.err.println("Failed to compile shader! Log: " + log);
            }
        }
    }

    public int getHandle() {
        return handle;
    }

    public void delete() {
        glDeleteShader(handle);
    }
}
