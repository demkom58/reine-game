package com.crown.graphic.gl.buffer;

import com.crown.graphic.gl.attribute.GlVertexFormat;

import java.nio.ByteBuffer;

public record VerticesData(GlVertexFormat<?> format, ByteBuffer buffer) {
    public int vertexCount() {
        int stride = format.getStride();
        if (stride == 0) {
            return 0;
        }

        return buffer.remaining() / stride;
    }
}
