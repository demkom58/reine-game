package com.crown.graphic.unit;

import com.crown.graphic.gl.array.GlVertexArray;
import com.crown.graphic.gl.buffer.GlMutableBuffer;
import com.crown.graphic.util.Destroyable;

public interface Mesh extends Destroyable {
    void bind();

    void unbind();

    void draw();

    int mode();

    int vertexCount();

    GlVertexArray vao();

    GlMutableBuffer[] vbos();
}
