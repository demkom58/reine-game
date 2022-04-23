package com.reine.util;

public enum Axis {
    X(Plane.HORIZONTAL),
    Y(Plane.VERTICAL),
    Z(Plane.HORIZONTAL);

    private final Plane plane;

    Axis(Plane plane) {
        this.plane = plane;
    }

    public Plane getPlane() {
        return plane;
    }
}
