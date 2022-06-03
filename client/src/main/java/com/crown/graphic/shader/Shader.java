package com.crown.graphic.shader;

import com.crown.graphic.util.Destroyable;
import com.crown.graphic.util.ShaderCompilationException;
import com.google.common.io.Resources;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Shader implements Destroyable {
    private final int handle;

    public Shader(URL resource, boolean vertex, String... defines) {
        try {
            this.handle = glCreateShader(vertex ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
            final String src = String.join("\n", defines)
                    + "\n" + Resources.toString(resource, StandardCharsets.UTF_8);

            glShaderSource(handle, src);
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

    public Shader(boolean vertex, String... defines) {
        this.handle = glCreateShader(vertex ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
        glShaderSource(handle, defines);
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

    @Override
    public void destroy() {
        glDeleteShader(handle);
    }
}
