package com.crown.graphic.gl.buffer;

import com.crown.graphic.gl.attribute.GlVertexFormat;

import java.nio.ByteBuffer;

public record VerticesData(GlVertexFormat<?> format, ByteBuffer buffer) {
}
