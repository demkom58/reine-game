package com.crown.graphic.unit;

import com.crown.graphic.util.Destroyable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import java.nio.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public record Mesh(int mode, int vertexCount, int vaoId, int eboId, int[] usedVbo) implements Destroyable {
    public static final Mesh EMPTY = Mesh.triangles().build();

    public void bind() {
        glBindVertexArray(vaoId);
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

        for (int vboId : usedVbo) {
            glDeleteBuffers(vboId);
        }

        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
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
        private int eboId = -1;
        private int vertices = -1;
        private final IntList vbo = new IntArrayList();

        private final int mode;

        private Builder(int mode) {
            this.mode = mode;

            this.vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);
        }

        public Builder indices(IntBuffer indices) {
            this.vertices = indices.remaining();

            eboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            return this;
        }

        public Builder positions(int index, FloatBuffer positions, int count, boolean normalized) {
            if (this.vertices == -1) {
                vertices = positions.remaining() / count;
            }

            int posVboId = glGenBuffers();
            vbo.add(posVboId);

            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
            glVertexAttribPointer(index, count, GL_FLOAT, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder positions(int index, IntBuffer positions, int count, boolean normalized) {
            if (this.vertices == -1) {
                vertices = positions.remaining() / 3;
            }

            int posVboId = glGenBuffers();
            vbo.add(posVboId);

            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
            glVertexAttribPointer(index, count, GL_INT, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, ByteBuffer values, int count) {
            int vboId = glGenBuffers();
            vbo.add(vboId);

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, GL_STATIC_DRAW);
            glVertexAttribIPointer(index, count, GL_BYTE, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, ShortBuffer values, int count) {
            int vboId = glGenBuffers();
            vbo.add(vboId);

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, GL_STATIC_DRAW);
            glVertexAttribIPointer(index, count, GL_SHORT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attributeUnsigned(int index, IntBuffer values, int count) {
            int vboId = glGenBuffers();
            vbo.add(vboId);

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, GL_STATIC_DRAW);
            glVertexAttribIPointer(index, count, GL_UNSIGNED_INT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, FloatBuffer values, int count, boolean normalized) {
            int vboId = glGenBuffers();
            vbo.add(vboId);

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, GL_STATIC_DRAW);
            glVertexAttribPointer(index, count, GL_FLOAT, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, DoubleBuffer values, int count, boolean normalized) {
            int vboId = glGenBuffers();
            vbo.add(vboId);

            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, values, GL_STATIC_DRAW);
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

            return new Mesh(mode, vertices, vaoId, eboId, vbo.toIntArray());
        }
    }
}
