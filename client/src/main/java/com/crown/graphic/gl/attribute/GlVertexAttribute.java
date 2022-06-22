package com.crown.graphic.gl.attribute;

public record GlVertexAttribute(int index, int format, int count, int pointer, int size, int stride, boolean normalized, boolean integral) {

    public GlVertexAttribute(int index, GlDataType format, int count, boolean normalized, int pointer, int stride, boolean integral) {
        this(index, format.id(), count, pointer, format.size() * count, stride, normalized, integral);
    }

}
