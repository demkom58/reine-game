package com.crown.graphic.gl.array;

import com.crown.graphic.gl.GlObject;

import static org.lwjgl.opengl.GL30.*;

public class GlVertexArray extends GlObject {
    public GlVertexArray() {
        this.setHandle(glGenVertexArrays());
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void bind() {
        glBindVertexArray(this.getHandle());
    }

    @Override
    public void destroy() {
        glDeleteVertexArrays(this.getHandle());
        this.invalidateHandle();
    }

}
