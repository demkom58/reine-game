package com.crown.graphic.unit;

import com.crown.graphic.gl.array.GlVertexArray;
import com.crown.graphic.gl.attribute.GlVertexFormat;
import com.crown.graphic.gl.buffer.GlMutableBuffer;
import com.crown.graphic.gl.buffer.VerticesData;
import com.crown.graphic.util.Destroyable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.nio.*;

import static org.lwjgl.opengl.GL33.*;

public record SplitMesh(int mode, int vertexCount, GlVertexArray vao, GlMutableBuffer ebo, GlMutableBuffer[] vbos) implements Mesh {
    public static final SplitMesh EMPTY = SplitMesh.triangles().build();

    @Override
    public void bind() {
        vao.bind();
    }

    @Override
    public void unbind() {
        vao.unbind();
    }

    @Override
    public void draw() {
        if (ebo == null) {
            glDrawArrays(mode, 0, vertexCount);
        } else {
            glDrawElements(mode, vertexCount, GL_UNSIGNED_INT, 0);
        }
    }

    @Override
    public void destroy() {
        vao.unbind();

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        if (ebo != null) {
            ebo.destroy();
        }

        for (GlMutableBuffer vbo : vbos) {
            vbo.destroy();
        }

        // Delete VAO
        glBindVertexArray(0);
        vao.destroy();
    }

    public static SplitMesh of(int mode, int usage, VerticesData data) {
        GlVertexFormat<?> format = data.format();
        GlVertexArray vao = new GlVertexArray();
        GlMutableBuffer vbo = new GlMutableBuffer(usage);

        vao.bind();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.upload(GL_ARRAY_BUFFER, data.buffer());
        format.bindVertexAttributes();
        format.enableVertexAttributes();
        vbo.unbind(GL_ARRAY_BUFFER);
        vao.unbind();

        return new SplitMesh(mode, data.vertexCount(), vao, null, new GlMutableBuffer[] { vbo });
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
        private final GlVertexArray vao;
        private GlMutableBuffer ebo;
        private int vertices;
        private final ObjectList<GlMutableBuffer> vbos;

        private final int mode;

        private Builder(int mode) {
            this.mode = mode;

            this.vao = new GlVertexArray();
            this.ebo = null;
            this.vertices = -1;
            this.vbos = new ObjectArrayList<>();

            vao.bind();
        }

        private Builder(int mode, GlVertexArray vao, GlMutableBuffer ebo, int vertices, ObjectList<GlMutableBuffer> vbos) {
            this.mode = mode;

            this.vao = vao;
            this.ebo = ebo;
            this.vertices = vertices;
            this.vbos = vbos;

            vao.bind();
        }

        public Builder indices(IntBuffer indices) {
            return indices(indices, GL_STATIC_DRAW);
        }

        public Builder indices(IntBuffer indices, int usage) {
            this.vertices = indices.remaining();

            ebo = new GlMutableBuffer(GL_STATIC_DRAW);
            ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
            ebo.upload(GL_ELEMENT_ARRAY_BUFFER, indices);

            return this;
        }

        public Builder positions(int index, FloatBuffer positions, int count, boolean normalized) {
            return positions(index, positions, count, normalized, GL_STATIC_DRAW);
        }

        public Builder positions(int index, FloatBuffer positions, int count, boolean normalized, int usage) {
            if (this.vertices == -1) {
                vertices = positions.remaining() / count;
            }

            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, positions);

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

            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, positions);

            glVertexAttribIPointer(index, count, GL_INT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, ByteBuffer values, int count) {
            return attribute(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, ByteBuffer values, int count, int usage) {
            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, values);

            glVertexAttribIPointer(index, count, GL_BYTE, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, IntBuffer values, int count) {
            return attribute(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, IntBuffer values, int count, int usage) {
            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, values);

            glVertexAttribIPointer(index, count, GL_INT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, ShortBuffer values, int count) {
            return attribute(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, ShortBuffer values, int count, int usage) {
            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, values);

            glVertexAttribIPointer(index, count, GL_SHORT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attributeUnsigned(int index, IntBuffer values, int count) {
            return attributeUnsigned(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attributeUnsigned(int index, IntBuffer values, int count, int usage) {
            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, values);

            glVertexAttribIPointer(index, count, GL_UNSIGNED_INT, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attributeUnsigned(int index, ByteBuffer values, int count) {
            return attributeUnsigned(index, values, count, GL_STATIC_DRAW);
        }

        public Builder attributeUnsigned(int index, ByteBuffer values, int count, int usage) {
            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, values);

            glVertexAttribIPointer(index, count, GL_UNSIGNED_BYTE, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, FloatBuffer values, int count, boolean normalized) {
            return attribute(index, values, count, normalized, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, FloatBuffer values, int count, boolean normalized, int usage) {
            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, values);

            glVertexAttribPointer(index, count, GL_FLOAT, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attribute(int index, DoubleBuffer values, int count, boolean normalized) {
            return attribute(index, values, count, normalized, GL_STATIC_DRAW);
        }

        public Builder attribute(int index, DoubleBuffer values, int count, boolean normalized, int usage) {
            GlMutableBuffer vbo = computeVBO(index, usage);

            vbo.bind(GL_ARRAY_BUFFER);
            vbo.upload(GL_ARRAY_BUFFER, values);

            glVertexAttribPointer(index, count, GL_DOUBLE, normalized, 0, 0);
            glEnableVertexAttribArray(index);

            return this;
        }

        public Builder attributeDivisor(int index, int divisor) {
            glVertexAttribDivisor(index, divisor);
            return this;
        }

        private GlMutableBuffer computeVBO(int index, int hints) {
            if (vbos.size() <= index) {
                GlMutableBuffer vbo = new GlMutableBuffer(hints);
                vbos.add(index, vbo);
                return vbo;
            }

            return vbos.get(index);
        }

        public SplitMesh build() {
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            vao.unbind();

            return new SplitMesh(mode, vertices, vao, ebo, vbos.toArray(new GlMutableBuffer[0]));
        }
    }
}
