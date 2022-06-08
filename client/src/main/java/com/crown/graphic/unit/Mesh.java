package com.crown.graphic.unit;

import com.crown.graphic.util.Destroyable;
import it.unimi.dsi.fastutil.ints.*;

import java.nio.*;

import static org.lwjgl.opengl.GL33.*;

public record Mesh(int mode, int vertexCount, int vaoId, int eboId, Int2IntMap usedVbo) implements Destroyable {
    public static final Mesh EMPTY = Mesh.triangles().build();

    public void bind() {
        glBindVertexArray(vaoId);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void draw() {
        if (eboId == -1) {
            glDrawArrays(mode, 0, vertexCount);
        } else {
            glDrawElements(mode, vertexCount, GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public void destroy() {
        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        for (int vboId : usedVbo.values()) {
            glDeleteBuffers(vboId);
        }

        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public Builder modify() {
        if (true) {
            throw new UnsupportedOperationException("Not implemented yet!");
        }

        return new Modifier(mode, vaoId, eboId, vertexCount, usedVbo, this);
    }

    public static Builder builder(int mode) {
        return new Builder(mode);
    }

    public static Builder triangles() {
        return new Builder(GL_TRIANGLES);
    }

    public static Builder quads() {
        return new Builder(GL_QUADS);
    }

    public static class Builder {
        private final int vaoId;
        private int eboId;
        private int vertices;
        private final Int2IntMap vbo;

        private final int mode;

        private Builder(int mode) {
            this.mode = mode;

            this.vaoId = glGenVertexArrays();
            this.eboId = -1;
            this.vertices = -1;
            this.vbo = new Int2IntOpenHashMap();
            glBindVertexArray(vaoId);
        }

        private Builder(int mode, int vaoId, int eboId, int vertices, Int2IntMap vbo) {
            this.mode = mode;
            this.vaoId = vaoId;
            this.eboId = eboId;
            this.vertices = vertices;
            this.vbo = vbo;
            glBindVertexArray(vaoId);
        }

        public Builder indices(IntBuffer indices) {
            return indices(indices, GL_STATIC_DRAW);
        }

        public Builder indices(IntBuffer indices, int usage) {
            this.vertices = indices.remaining();

            eboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, usage);

            return this;
        }

        public Builder positions(int index, FloatBuffer positions, int count, boolean normalized) {
            return positions(index, positions, count, normalized, GL_STATIC_DRAW);
        }

        public Builder positions(int index, FloatBuffer positions, int count, boolean normalized, int usage) {
            if (this.vertices == -1) {
                vertices = positions.remaining() / count;
            }

            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, positions, usage);
            glVertexAttribPointer(index, count, GL_FLOAT, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder positions(int index, IntBuffer positions, int count) {
            return positions(index, positions, count, GL_STATIC_DRAW);
        }

        public Builder positions(int index, IntBuffer positions, int count, int usage) {
            if (this.vertices == -1) {
                vertices = positions.remaining() / 3;
            }

            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, positions, usage);
            glVertexAttribIPointer(index, count, GL_INT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, ByteBuffer values, int count) {
            return attribute(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, ByteBuffer values, int count, int usage) {
            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, usage);
            glVertexAttribIPointer(index, count, GL_BYTE, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, IntBuffer values, int count) {
            return attribute(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, IntBuffer values, int count, int usage) {
            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, usage);
            glVertexAttribIPointer(index, count, GL_INT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, ShortBuffer values, int count) {
            return attribute(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, ShortBuffer values, int count, int usage) {
            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, usage);
            glVertexAttribIPointer(index, count, GL_SHORT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attributeUnsigned(int index, IntBuffer values, int count) {
            return attributeUnsigned(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attributeUnsigned(int index, IntBuffer values, int count, int usage) {
            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, usage);
            glVertexAttribIPointer(index, count, GL_UNSIGNED_INT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attributeUnsigned(int index, ByteBuffer values, int count) {
            return attributeUnsigned(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attributeUnsigned(int index, ByteBuffer values, int count, int usage) {
            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, usage);
            glVertexAttribIPointer(index, count, GL_UNSIGNED_BYTE, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, FloatBuffer values, int count, boolean normalized) {
            return attribute(index, values, count, normalized, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, FloatBuffer values, int count, boolean normalized, int usage) {
            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, usage);
            glVertexAttribPointer(index, count, GL_FLOAT, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, DoubleBuffer values, int count, boolean normalized) {
            return attribute(index, values, count, normalized, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, DoubleBuffer values, int count, boolean normalized, int usage) {
            int vboId = vbo.computeIfAbsent(index, i -> glGenBuffers());

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, usage);
            glVertexAttribPointer(index, count, GL_DOUBLE, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attributeDivisor(int index, int divisor) {
            glVertexAttribDivisor(index, divisor);
            return this;
        }

        public Mesh build() {
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            return new Mesh(mode, vertices, vaoId, eboId, vbo);
        }
    }

    public static class Modifier extends Builder {
        private final Mesh mesh;

        public Modifier(int mode, int vaoId, int eboId, int vertices, Int2IntMap vbo, Mesh mesh) {
            super(mode, vaoId, eboId, vertices, vbo);
            this.mesh = mesh;
        }

        public Mesh build() {
            return mesh;
        }
    }
}
