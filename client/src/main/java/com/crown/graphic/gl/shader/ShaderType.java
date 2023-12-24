package com.crown.graphic.gl.shader;

import static org.lwjgl.opengl.GL45.*;

public enum ShaderType {
    VERTEX(GL_VERTEX_SHADER),
    FRAGMENT(GL_FRAGMENT_SHADER),
    COMPUTE(GL_COMPUTE_SHADER),
    ;

    public final int id;

    ShaderType(int id) {
        this.id = id;
    }
}
