package com.crown.graphic.unit;

import com.crown.graphic.gl.array.GlVertexArray;
import com.crown.graphic.gl.attribute.GlVertexFormat;
import com.crown.graphic.gl.buffer.GlMutableBuffer;
import com.crown.graphic.gl.buffer.VerticesData;

import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

public record ComposedMesh(int mode, int vertexCount, GlVertexArray vao, GlMutableBuffer vbo) implements Mesh {
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
        glDrawArrays(mode, 0, vertexCount);
    }

    @Override
    public GlMutableBuffer[] vbos() {
        return new GlMutableBuffer[] {vbo};
    }

    @Override
    public void destroy() {
        vbo.unbind(GL_VERTEX_ARRAY);
        vao.unbind();

        vbo.destroy();
        vao.destroy();
    }

    public static ComposedMesh of(int mode, int usage, VerticesData data) {
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

        return new ComposedMesh(mode, data.vertexCount(), vao, vbo);
    }
}
