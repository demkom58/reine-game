package com.crown.graphic.gl.shader;

import com.crown.graphic.gl.GlObject;
import com.google.common.io.Resources;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class GlShader extends GlObject {

    public GlShader(ShaderType type, URL resource, Collection<String> defines) {
        try {
            int handle = glCreateShader(type.id);
            setHandle(handle);

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

    public GlShader(ShaderType type, Collection<String> defines) {
        int handle = glCreateShader(type.id);
        setHandle(handle);

        glShaderSource(handle, defines.toArray(new String[0]));
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

    @Override
    public void destroy() {
        glDeleteShader(getHandle());
        invalidateHandle();
    }
}
