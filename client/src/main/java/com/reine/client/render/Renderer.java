package com.reine.client.render;

import org.joml.Matrix4f;

public class Renderer {
    public final Matrix4f oneMainMatrix = new Matrix4f().identity();
    public final float[] modelBuffer = new float[4 * 4];
    public final Matrix4f modelMatrix = new Matrix4f().identity();



}
