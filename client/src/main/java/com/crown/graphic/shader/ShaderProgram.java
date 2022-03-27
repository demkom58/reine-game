package com.crown.graphic.shader;

import com.crown.graphic.util.Destroyable;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class ShaderProgram implements Destroyable {
    private final int handle;

    public ShaderProgram(Shader... shaders) {
        this.handle = glCreateProgram();

        //noinspection ForLoopReplaceableByForEach
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

    public void setUniform1i(String name, int value1) {
        glUniform1i(glGetUniformLocation(handle, name), value1);
    }

    public void setUniform1iv(String name, IntBuffer values) {
        glUniform1iv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform1iv(String name, int[] values) {
        glUniform1iv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform2i(String name, int value1, int value2) {
        glUniform2i(glGetUniformLocation(handle, name), value1, value2);
    }

    public void setUniform2iv(String name, IntBuffer values) {
        glUniform2iv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform2iv(String name, int[] values) {
        glUniform2iv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform3i(String name, int value1, int value2, int value3) {
        glUniform3i(glGetUniformLocation(handle, name), value1, value2, value3);
    }

    public void setUniform3iv(String name, IntBuffer values) {
        glUniform3iv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform3iv(String name, int[] values) {
        glUniform3iv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform4i(String name, int value1, int value2, int value3, int value4) {
        glUniform4i(glGetUniformLocation(handle, name), value1, value2, value3, value4);
    }

    public void setUniform4iv(String name, IntBuffer values) {
        glUniform4iv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform4iv(String name, int[] values) {
        glUniform4iv(glGetUniformLocation(handle, name), values);
    }

    public float getUniformi(String name) {
        return glGetUniformi(handle, glGetUniformLocation(handle, name));
    }

    public void getUniformiv(String name, int[] output) {
        glGetUniformiv(handle, glGetUniformLocation(handle, name), output);
    }

    public void getUniformiv(String name, IntBuffer output) {
        glGetUniformiv(handle, glGetUniformLocation(handle, name), output);
    }

    public void setUniform1f(String name, float value1) {
        glUniform1f(glGetUniformLocation(handle, name), value1);
    }

    public void setUniform1fv(String name, FloatBuffer values) {
        glUniform1fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform1fv(String name, float[] values) {
        glUniform1fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform2f(String name, float value1, float value2) {
        glUniform2f(glGetUniformLocation(handle, name), value1, value2);
    }

    public void setUniform2fv(String name, FloatBuffer values) {
        glUniform2fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform2fv(String name, float[] values) {
        glUniform2fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform3f(String name, float value1, float value2, float value3) {
        glUniform3f(glGetUniformLocation(handle, name), value1, value2, value3);
    }

    public void setUniform3fv(String name, FloatBuffer values) {
        glUniform3fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform3fv(String name, float[] values) {
        glUniform3fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform4f(String name, float value1, float value2, float value3, float value4) {
        glUniform4f(glGetUniformLocation(handle, name), value1, value2, value3, value4);
    }

    public void setUniform4fv(String name, FloatBuffer values) {
        glUniform4fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniform4fv(String name, float[] values) {
        glUniform4fv(glGetUniformLocation(handle, name), values);
    }

    public void setUniformMatrix2fv(String name, boolean transpose, FloatBuffer values) {
        glUniformMatrix2fv(glGetUniformLocation(handle, name), transpose, values);
    }

    public void setUniformMatrix2fv(String name, boolean transpose, float[] values) {
        glUniformMatrix2fv(glGetUniformLocation(handle, name), transpose, values);
    }

    public void setUniformMatrix3fv(String name, boolean transpose, FloatBuffer values) {
        glUniformMatrix3fv(glGetUniformLocation(handle, name), transpose, values);
    }

    public void setUniformMatrix3fv(String name, boolean transpose, float[] values) {
        glUniformMatrix3fv(glGetUniformLocation(handle, name), transpose, values);
    }

    public void setUniformMatrix4fv(String name, boolean transpose, FloatBuffer values) {
        glUniformMatrix4fv(glGetUniformLocation(handle, name), transpose, values);
    }

    public void setUniformMatrix4fv(String name, boolean transpose, float[] values) {
        glUniformMatrix4fv(glGetUniformLocation(handle, name), transpose, values);
    }

    public float getUniformf(String name) {
        return glGetUniformf(handle, glGetUniformLocation(handle, name));
    }

    public void getUniformfv(String name, float[] output) {
        glGetUniformfv(handle, glGetUniformLocation(handle, name), output);
    }

    public void getUniformfv(String name, FloatBuffer output) {
        glGetUniformfv(handle, glGetUniformLocation(handle, name), output);
    }

    public void getActiveUniform(String name, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer nameBuff) {
        glGetActiveUniform(handle, glGetUniformLocation(handle, name) + index, length, size, type, nameBuff);
    }

    public void getActiveUniform(String name, int index, IntBuffer length, IntBuffer type) {
        glGetActiveUniform(handle, glGetUniformLocation(handle, name) + index, length, type);
    }

    public void getActiveUniform(String name, int index, int maxLength, IntBuffer length, IntBuffer type) {
        glGetActiveUniform(handle, glGetUniformLocation(handle, name) + index, maxLength, length, type);
    }

    public void getActiveUniform(String name, int index, @Nullable int[] length, int[] size, int[] type, ByteBuffer nameBuff) {
        glGetActiveUniform(handle, glGetUniformLocation(handle, name) + index, length, size, type, nameBuff);
    }

    public void use() {
        glUseProgram(handle);
    }

    @Override
    public void destroy() {
        glDeleteProgram(handle);
    }

}
