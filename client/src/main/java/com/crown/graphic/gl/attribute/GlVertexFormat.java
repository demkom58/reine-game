package com.crown.graphic.gl.attribute;

import java.util.EnumMap;

import static org.lwjgl.opengl.GL33.*;

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
            if (attrib.integral()) {
                glVertexAttribIPointer(attrib.index(), attrib.count(), attrib.format(),
                        attrib.stride(), attrib.pointer());
            } else {
                glVertexAttribPointer(attrib.index(), attrib.count(), attrib.format(), attrib.normalized(),
                        attrib.stride(), attrib.pointer());
            }
        }
    }

    @Override
    public String toString() {
        return String.format("GlVertexFormat<%s>{attributes=%d,stride=%d}", this.attributeEnum.getName(),
                this.attributesKeyed.size(), this.stride);
    }


    public static <T extends Enum<T>> Builder<T> builder(Class<T> type, int stride) {
        return new Builder<>(type, stride);
    }

    public static class Builder<T extends Enum<T>> {
        private final EnumMap<T, GlVertexAttribute> attributes;
        private final Class<T> type;
        private final int stride;

        public Builder(Class<T> type, int stride) {
            this.type = type;
            this.attributes = new EnumMap<>(type);
            this.stride = stride;
        }

        public Builder<T> add(T type, int pointer, GlDataType format, int count, boolean normalized) {
            return this.add(type, new GlVertexAttribute(this.attributes.size(), format, count, normalized, pointer, this.stride, format.integral()));
        }

        private Builder<T> add(T type, GlVertexAttribute attribute) {
            if (attribute.pointer() >= this.stride) {
                throw new IllegalArgumentException("Element starts outside vertex format");
            }

            if (attribute.pointer() + attribute.size() > this.stride) {
                throw new IllegalArgumentException("Element extends outside vertex format");
            }

            if (this.attributes.put(type, attribute) != null) {
                throw new IllegalStateException("Generic attribute " + type.name() + " already defined in vertex format");
            }

            return this;
        }

        public GlVertexFormat<T> build() {
            int size = 0;

            for (T key : this.type.getEnumConstants()) {
                GlVertexAttribute attribute = this.attributes.get(key);

                if (attribute == null) {
                    throw new NullPointerException("Generic attribute not assigned to enumeration " + key.name());
                }

                size = Math.max(size, attribute.pointer() + attribute.size());
            }

            // The stride must be large enough to cover all attributes. This still allows for additional padding
            // to be added to the end of the vertex to accommodate alignment restrictions.
            if (this.stride < size) {
                throw new IllegalArgumentException("Stride is too small");
            }

            return new GlVertexFormat<>(this.type, this.attributes, this.stride);
        }
    }
}
