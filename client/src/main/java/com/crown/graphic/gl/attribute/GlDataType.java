package com.crown.graphic.gl.attribute;

import static org.lwjgl.opengl.GL11.*;

public record GlDataType(int id, int size, boolean integral) {
    public static final GlDataType DOUBLE = new GlDataType(GL_DOUBLE, 8, false);
    public static final GlDataType FLOAT = new GlDataType(GL_FLOAT, 4, false);
    public static final GlDataType INT = new GlDataType(GL_INT, 4, true);
    public static final GlDataType UNSIGNED_INT = new GlDataType(GL_UNSIGNED_INT, 4, true);
    public static final GlDataType SHORT = new GlDataType(GL_SHORT, 2, true);
    public static final GlDataType UNSIGNED_SHORT = new GlDataType(GL_UNSIGNED_SHORT, 2, true);
    public static final GlDataType BYTE = new GlDataType(GL_BYTE, 1, true);
    public static final GlDataType UNSIGNED_BYTE = new GlDataType(GL_UNSIGNED_BYTE, 1, true);
}
