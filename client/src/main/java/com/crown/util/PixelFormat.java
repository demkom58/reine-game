package com.crown.util;


import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public enum PixelFormat {
    RGB(3, 8, 255, GL11.GL_RGB) {
        @Override
        public void putRGBA(int rgba, ByteBuffer buffer) {
            buffer.put((byte) (rgba & 0xFF));
            buffer.put((byte) ((rgba >> 8) & 0xFF));
            buffer.put((byte) ((rgba >> 16) & 0xFF));
        }

        @Override
        public int getRGBA(ByteBuffer buffer) {
            int result = 0;
            result |= buffer.get() & 0x0000FF;
            result |= (buffer.get() << 8)  & 0x00FF00;
            result |= (buffer.get() << 16) & 0xFF0000;
            return result;
        }

        @Override
        public void putRGBA(int index, int rgba, ByteBuffer buffer) {
            buffer.put(index, (byte) (rgba & 0xFF));
            buffer.put(index + 1, (byte) ((rgba >> 8) & 0xFF));
            buffer.put(index + 2, (byte) ((rgba >> 16) & 0xFF));
        }

        @Override
        public int getRGBA(int index, ByteBuffer buffer) {
            int result = 0;
            result |= buffer.get(index) & 0x0000FF;
            result |= (buffer.get(index + 1) << 8) & 0x00FF00;
            result |= (buffer.get(index + 2) << 16) & 0xFF0000;
            return result;
        }
    },
    RGBA(4, 8,255, GL11.GL_RGBA) {
        @Override
        public void putRGBA(int rgba, ByteBuffer buffer) {
            buffer.put((byte) (rgba & 0xFF));
            buffer.put((byte) ((rgba >> 8) & 0xFF));
            buffer.put((byte) ((rgba >> 16) & 0xFF));
            buffer.put((byte) ((rgba >> 24) & 0xFF));
        }

        @Override
        public int getRGBA(ByteBuffer buffer) {
            int result = 0;
            result |= buffer.get() & 0x0000FF;
            result |= (buffer.get() << 8) & 0x00FF00;
            result |= (buffer.get() << 16) & 0xFF0000;
            result |= (buffer.get() << 24);
            return result;
        }

        @Override
        public void putRGBA(int index, int rgba, ByteBuffer buffer) {
            buffer.put(index, (byte) (rgba & 0xFF));
            buffer.put(index + 1, (byte) ((rgba >> 8) & 0xFF));
            buffer.put(index + 2, (byte) ((rgba >> 16) & 0xFF));
            buffer.put(index + 3, (byte) ((rgba >> 24) & 0xFF));
        }

        @Override
        public int getRGBA(int index, ByteBuffer buffer) {
            int result = 0;
            result |= buffer.get(index) & 0x0000FF;
            result |= (buffer.get(index + 1) << 8) & 0x00FF00;
            result |= (buffer.get(index + 2) << 16) & 0xFF0000;
            result |= (buffer.get(index + 3) << 24);
            return result;
        }
    };

    public final int channels;
    public final int channelBits;
    public final int channelMax;
    public final int glType;

    PixelFormat(int channels, int channelBits, int channelMax, int glType) {
        this.channels = channels;
        this.channelBits = channelBits;
        this.channelMax = channelMax;
        this.glType = glType;
    }

    public abstract void putRGBA(int rgba, ByteBuffer buffer);

    public abstract int getRGBA(ByteBuffer buffer);

    public abstract void putRGBA(int index, int rgba, ByteBuffer buffer);

    public abstract int getRGBA(int index, ByteBuffer buffer);
}