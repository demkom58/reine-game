package com.reine.util;

import org.joml.Vector3f;

public enum Axis {
    X(Plane.HORIZONTAL, new Vector3f(1.0f, 0.0f, 0.0f)),
    Y(Plane.VERTICAL, new Vector3f(0.0f, 1.0f, 0.0f)),
    Z(Plane.HORIZONTAL, new Vector3f(0.0f, 0.0f, 1.0f));

    private final Plane plane;
    private final Vector3f vector;

    Axis(Plane plane, Vector3f vector) {
        this.plane = plane;
        this.vector = vector;
    }

    public Plane getPlane() {
        return plane;
    }

    public Vector3f getVector() {
        return new Vector3f(vector);
    }
}
