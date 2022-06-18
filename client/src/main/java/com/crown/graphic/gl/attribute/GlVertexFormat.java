package com.crown.graphic.gl.attribute;

import java.util.EnumMap;

import static org.lwjgl.opengl.GL20.*;

public class GlVertexFormat<T extends Enum<T>> {
    private final Class<T> attributeEnum;
    private final EnumMap<T, GlVertexAttribute> attributesKeyed;
    private final GlVertexAttribute[] attributesArray;

    private final int stride;

    public GlVertexFormat(Class<T> attributeEnum, EnumMap<T, GlVertexAttribute> attributesKeyed, int stride) {
        this.attributeEnum = attributeEnum;
        this.attributesKeyed = attributesKeyed;
        this.attributesArray = attributesKeyed.values().toArray(new GlVertexAttribute[0]);
        this.stride = stride;
    }

    public GlVertexAttribute getAttribute(T name) {
        GlVertexAttribute attr = this.attributesKeyed.get(name);

        if (attr == null) {
            throw new NullPointerException("No attribute exists for " + name.toString());
        }

        return attr;
    }

    public int getStride() {
        return this.stride;
    }

    public GlVertexAttribute[] getAttributesArray() {
        return this.attributesArray;
    }

    public void enableVertexAttributes() {
        for (GlVertexAttribute binding : this.getAttributesArray()) {
            glEnableVertexAttribArray(binding.index());
        }
    }

    public void disableVertexAttributes() {
        for (GlVertexAttribute binding : this.getAttributesArray()) {
            glDisableVertexAttribArray(binding.index());
        }
    }

    public void bindVertexAttributes() {
        for (GlVertexAttribute attrib : this.getAttributesArray()) {
            glVertexAttribPointer(attrib.index(), attrib.count(), attrib.format(),
                    attrib.normalized(), attrib.stride(), attrib.pointer());
        }
    }

    @Override
    public String toString() {
        return String.format("GlVertexFormat<%s>{attributes=%d,stride=%d}", this.attributeEnum.getName(),
                this.attributesKeyed.size(), this.stride);
    }
}
