package com.crown.graphic.gl.buffer;

import java.nio.*;

import static org.lwjgl.opengl.GL15.*;

public class GlMutableBuffer extends GlBuffer {
    private final int hints;

    public GlMutableBuffer(int hints) {
        this.hints = hints;
    }

    @Override
    public void upload(int target, ByteBuffer buf) {
        glBufferData(target, buf, this.hints);
    }

    @Override
    public void upload(int target, ShortBuffer buf) {
        glBufferData(target, buf, this.hints);
    }

    @Override
    public void upload(int target, IntBuffer buf) {
        glBufferData(target, buf, this.hints);
    }

    @Override
    public void upload(int target, LongBuffer buf) {
        glBufferData(target, buf, this.hints);
    }

    @Override
    public void upload(int target, FloatBuffer buf) {
        glBufferData(target, buf, this.hints);
    }

    @Override
    public void upload(int target, DoubleBuffer buf) {
        glBufferData(target, buf, this.hints);
    }

    @Override
    public void allocate(int target, long size) {
        glBufferData(target, size, this.hints);
    }

    public void invalidate(int target) {
        this.allocate(target, 0);
    }
}
