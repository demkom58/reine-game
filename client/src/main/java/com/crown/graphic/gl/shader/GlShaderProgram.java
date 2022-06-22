package com.crown.graphic.gl.shader;

import com.crown.graphic.gl.GlObject;
import org.lwjgl.system.MemoryStack;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class GlShaderProgram extends GlObject {

    public GlShaderProgram(GlShader... shaders) {
        int handle = glCreateProgram();
        setHandle(handle);

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < shaders.length; i++) {
            glAttachShader(getHandle(), shaders[i].getHandle());
        }

        glLinkProgram(handle);

        checkCompilation();
    }

    public GlShaderProgram(GlShader vertex, GlShader frag) {
        int handle = glCreateProgram();
        setHandle(handle);
        
        glAttachShader(getHandle(), vertex.getHandle());
        glAttachShader(getHandle(), frag.getHandle());
        glLinkProgram(handle);

        checkCompilation();
    }

    private void checkCompilation() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer status = stack.mallocInt(1);

            int handle = getHandle();
            glGetProgramiv(getHandle(), GL_LINK_STATUS, status);
            if (status.get() == GL_FALSE) {
                String log = glGetProgramInfoLog(handle);
                System.err.println("Failed to compile shader program! Log: " + log);
            }
        }
    }

    public void setUniform1i(String name, int value1) {
        glUniform1i(glGetUniformLocation(getHandle(), name), value1);
    }

    public void setUniform1iv(String name, IntBuffer values) {
        glUniform1iv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform1iv(String name, int[] values) {
        glUniform1iv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform2i(String name, int value1, int value2) {
        glUniform2i(glGetUniformLocation(getHandle(), name), value1, value2);
    }

    public void setUniform2iv(String name, IntBuffer values) {
        glUniform2iv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform2iv(String name, int[] values) {
        glUniform2iv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform3i(String name, int value1, int value2, int value3) {
        glUniform3i(glGetUniformLocation(getHandle(), name), value1, value2, value3);
    }

    public void setUniform3iv(String name, IntBuffer values) {
        glUniform3iv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform3iv(String name, int[] values) {
        glUniform3iv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform4i(String name, int value1, int value2, int value3, int value4) {
        glUniform4i(glGetUniformLocation(getHandle(), name), value1, value2, value3, value4);
    }

    public void setUniform4iv(String name, IntBuffer values) {
        glUniform4iv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform4iv(String name, int[] values) {
        glUniform4iv(glGetUniformLocation(getHandle(), name), values);
    }

    public float getUniformi(String name) {
        return glGetUniformi(getHandle(), glGetUniformLocation(getHandle(), name));
    }

    public void getUniformiv(String name, int[] output) {
        glGetUniformiv(getHandle(), glGetUniformLocation(getHandle(), name), output);
    }

    public void getUniformiv(String name, IntBuffer output) {
        glGetUniformiv(getHandle(), glGetUniformLocation(getHandle(), name), output);
    }

    public void setUniform1f(String name, float value1) {
        glUniform1f(glGetUniformLocation(getHandle(), name), value1);
    }

    public void setUniform1f(int index, float value1) {
        glUniform1f(index, value1);
    }

    public void setUniform1fv(String name, FloatBuffer values) {
        glUniform1fv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform1fv(int index, FloatBuffer values) {
        glUniform1fv(index, values);
    }

    public void setUniform1fv(String name, float[] values) {
        glUniform1fv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform1fv(int index, float[] values) {
        glUniform1fv(index, values);
    }

    public void setUniform2f(String name, float value1, float value2) {
        glUniform2f(glGetUniformLocation(getHandle(), name), value1, value2);
    }
    public void setUniform2f(int index, float value1, float value2) {
        glUniform2f(index, value1, value2);
    }

    public void setUniform2fv(String name, FloatBuffer values) {
        glUniform2fv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform2fv(int index, FloatBuffer values) {
        glUniform2fv(index, values);
    }

    public void setUniform2fv(String name, float[] values) {
        glUniform2fv(glGetUniformLocation(getHandle(), name), values);
    }


    public void setUniform2fv(int index, float[] values) {
        glUniform2fv(index, values);
    }

    public void setUniform3f(String name, float value1, float value2, float value3) {
        glUniform3f(glGetUniformLocation(getHandle(), name), value1, value2, value3);
    }
    public void setUniform3f(int index, float value1, float value2, float value3) {
        glUniform3f(index, value1, value2, value3);
    }

    public void setUniform3fv(String name, FloatBuffer values) {
        glUniform3fv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform3fv(int index, FloatBuffer values) {
        glUniform3fv(index, values);
    }

    public void setUniform3fv(String name, float[] values) {
        glUniform3fv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform3fv(int index, float[] values) {
        glUniform3fv(index, values);
    }

    public void setUniform4f(String name, float value1, float value2, float value3, float value4) {
        glUniform4f(glGetUniformLocation(getHandle(), name), value1, value2, value3, value4);
    }
    public void setUniform4f(int index, float value1, float value2, float value3, float value4) {
        glUniform4f(index, value1, value2, value3, value4);
    }

    public void setUniform4fv(String name, FloatBuffer values) {
        glUniform4fv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform4fv(int index, FloatBuffer values) {
        glUniform4fv(index, values);
    }

    public void setUniform4fv(String name, float[] values) {
        glUniform4fv(glGetUniformLocation(getHandle(), name), values);
    }

    public void setUniform4fv(int index, float[] values) {
        glUniform4fv(index, values);
    }

    public void setUniformMatrix2fv(String name, boolean transpose, FloatBuffer values) {
        glUniformMatrix2fv(glGetUniformLocation(getHandle(), name), transpose, values);
    }
    public void setUniformMatrix2fv(int index, boolean transpose, FloatBuffer values) {
        glUniformMatrix2fv(index, transpose, values);
    }

    public void setUniformMatrix2fv(String name, boolean transpose, float[] values) {
        glUniformMatrix2fv(glGetUniformLocation(getHandle(), name), transpose, values);
    }

    public void setUniformMatrix2fv(int index, boolean transpose, float[] values) {
        glUniformMatrix2fv(index, transpose, values);
    }

    public void setUniformMatrix3fv(String name, boolean transpose, FloatBuffer values) {
        glUniformMatrix3fv(glGetUniformLocation(getHandle(), name), transpose, values);
    }

    public void setUniformMatrix3fv(int index, boolean transpose, FloatBuffer values) {
        glUniformMatrix3fv(index, transpose, values);
    }

    public void setUniformMatrix3fv(String name, boolean transpose, float[] values) {
        glUniformMatrix3fv(glGetUniformLocation(getHandle(), name), transpose, values);
    }

    public void setUniformMatrix3fv(int index, boolean transpose, float[] values) {
        glUniformMatrix3fv(index, transpose, values);
    }

    public void setUniformMatrix4fv(String name, boolean transpose, FloatBuffer values) {
        glUniformMatrix4fv(glGetUniformLocation(getHandle(), name), transpose, values);
    }

    public void setUniformMatrix4fv(int index, boolean transpose, FloatBuffer values) {
        glUniformMatrix4fv(index, transpose, values);
    }

    public void setUniformMatrix4fv(String name, boolean transpose, float[] values) {
        glUniformMatrix4fv(glGetUniformLocation(getHandle(), name), transpose, values);
    }

    public void setUniformMatrix4fv(int index, boolean transpose, float[] values) {
        glUniformMatrix4fv(index, transpose, values);
    }

    public void setUniformBlock(String name, int uniformBlockBinding) {
        glUniformBlockBinding(getHandle(), glGetUniformBlockIndex(getHandle(), name), uniformBlockBinding);
    }

    public float getUniformf(String name) {
        return glGetUniformf(getHandle(), glGetUniformLocation(getHandle(), name));
    }

    public void getUniformfv(String name, float[] output) {
        glGetUniformfv(getHandle(), glGetUniformLocation(getHandle(), name), output);
    }

    public void getUniformfv(String name, FloatBuffer output) {
        glGetUniformfv(getHandle(), glGetUniformLocation(getHandle(), name), output);
    }

    public void getActiveUniform(String name, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer nameBuff) {
        glGetActiveUniform(getHandle(), glGetUniformLocation(getHandle(), name) + index, length, size, type, nameBuff);
    }

    public void getActiveUniform(String name, int index, IntBuffer length, IntBuffer type) {
        glGetActiveUniform(getHandle(), glGetUniformLocation(getHandle(), name) + index, length, type);
    }

    public void getActiveUniform(String name, int index, int maxLength, IntBuffer length, IntBuffer type) {
        glGetActiveUniform(getHandle(), glGetUniformLocation(getHandle(), name) + index, maxLength, length, type);
    }

    public void getActiveUniform(String name, int index, @Nullable int[] length, int[] size, int[] type, ByteBuffer nameBuff) {
        glGetActiveUniform(getHandle(), glGetUniformLocation(getHandle(), name) + index, length, size, type, nameBuff);
    }

    public void use() {
        glUseProgram(getHandle());
    }

    @Override
    public void destroy() {
        glDeleteProgram(getHandle());
        invalidateHandle();
    }

}
