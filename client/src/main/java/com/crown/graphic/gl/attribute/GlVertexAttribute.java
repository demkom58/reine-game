package com.crown.graphic.gl.attribute;

import java.util.EnumMap;

public record GlVertexAttribute(int index, int format, int count, int pointer, int size, int stride, boolean normalized) {

    public GlVertexAttribute(int index, GlDataType format, int count, boolean normalized, int pointer, int stride) {
        this(index, format.id(), count, pointer, format.size() * count, stride, normalized);
    }

    public static <T extends Enum<T>> GlVertexAttribute.Builder<T> builder(Class<T> type, int stride) {
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
            return this.add(type, new GlVertexAttribute(this.attributes.size(), format, count, normalized, pointer, this.stride));
        }

        private Builder<T> add(T type, GlVertexAttribute attribute) {
            if (attribute.pointer >= this.stride) {
                throw new IllegalArgumentException("Element starts outside vertex format");
            }

            if (attribute.pointer + attribute.size > this.stride) {
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
