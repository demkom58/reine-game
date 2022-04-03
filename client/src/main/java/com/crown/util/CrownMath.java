package com.crown.util;

import org.joml.Matrix4f;

public final class CrownMath {
    public static Matrix4f createMatrix4fMainOnes() {
        return new Matrix4f(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
        );
    }
}
