package com.crown.graphic.gl.buffer;

import com.crown.graphic.gl.GlObject;

import java.nio.*;

import static org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL31.*;

public abstract class GlBuffer extends GlObject {
    protected int vertexCount = 0;

    protected GlBuffer() {
        this.setHandle(glGenBuffers());
    }

    public void unbind(int target) {
        glBindBuffer(target, 0);
    }

    public void bind(int target) {
        glBindBuffer(target, getHandle());
    }

    public void drawArrays(int mode) {
        this.drawArrays(mode, 0, vertexCount);
    }

    public void drawArrays(int mode, int first, int count) {
        glDrawArrays(mode, first, count);
    }

    public void upload(int target, VerticesData vertices) {
        final ByteBuffer buffer = vertices.buffer();
        this.vertexCount = buffer.remaining() / vertices.format().getStride();
        this.upload(target, buffer);
    }

    public void upload(int target, int offset, ByteBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void upload(int target, int offset, ShortBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void upload(int target, int offset, IntBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void upload(int target, int offset, LongBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void upload(int target, int offset, FloatBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void upload(int target, int offset, DoubleBuffer data) {
        glBufferSubData(target, offset, data);
    }

    public void bindRange(int target, int index, int offset, long size) {
        glBindBufferRange(target, index, getHandle(), offset, size);
    }

    public abstract void upload(int target, ByteBuffer buf);

    public abstract void upload(int target, ShortBuffer buf);

    public abstract void upload(int target, IntBuffer buf);

    public abstract void upload(int target, LongBuffer buf);

    public abstract void upload(int target, FloatBuffer buf);

    public abstract void upload(int target, DoubleBuffer buf);

    public abstract void allocate(int target, long size);

    public static void copy(GlBuffer src, GlBuffer dst, int readOffset, int writeOffset, int copyLen, int bufferSize) {
        src.bind(GL_COPY_READ_BUFFER);

        dst.bind(GL_COPY_WRITE_BUFFER);
        dst.allocate(GL_COPY_WRITE_BUFFER, bufferSize);

        glCopyBufferSubData(GL_COPY_READ_BUFFER, GL_COPY_WRITE_BUFFER, readOffset, writeOffset, copyLen);

        dst.unbind(GL_COPY_WRITE_BUFFER);
        src.unbind(GL_COPY_READ_BUFFER);
    }

    @Override
    public void destroy() {
        glDeleteBuffers(getHandle());
        this.invalidateHandle();
    }
}
