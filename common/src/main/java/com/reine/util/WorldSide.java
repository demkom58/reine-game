package com.reine.util;

import org.joml.Vector3f;

public enum WorldSide {
    DOWN(-1, 1, Direction.NEGATIVE, Axis.Y, new Vector3f(0, -1, 0), new Vector3f(1, 0, 1)),
    UP(-1, 0, Direction.POSITIVE, Axis.Y, new Vector3f(0, 1, 0), new Vector3f(1, 0, 1)),
    NORTH(2, 3, Direction.NEGATIVE, Axis.Z, new Vector3f(0, 0, -1), new Vector3f(1, 1, 0)),
    SOUTH(0, 2, Direction.POSITIVE, Axis.Z, new Vector3f(0, 0, 1), new Vector3f(1, 1, 0)),
    WEST(1, 5, Direction.NEGATIVE, Axis.X, new Vector3f(-1, 0, 0), new Vector3f(0, 1, 1)),
    EAST(3, 4, Direction.POSITIVE, Axis.X, new Vector3f(1, 0, 0), new Vector3f(0, 1, 1));

    private static final WorldSide[] vals = values();

    private final int horizontalOrder;
    private final int oppositeSideIdx;
    private final Direction direction;
    private final Axis axis;
    private final Vector3f vec;
    private final Vector3f spaceVec;

    private final byte mask;

    WorldSide(int horizontalOrder, int oppositeSideIdx, Direction direction, Axis axis, Vector3f vec, Vector3f spaceVec) {
        this.horizontalOrder = horizontalOrder;
        this.oppositeSideIdx = oppositeSideIdx;
        this.direction = direction;
        this.axis = axis;
        this.vec = vec;
        this.spaceVec = spaceVec;
        this.mask = (byte) (1 << ordinal());
    }

    public int horizontalOrder() {
        return horizontalOrder;
    }

    public int oppositeSideIdx() {
        return oppositeSideIdx;
    }

    public WorldSide opposite() {
        return vals[oppositeSideIdx];
    }

    public Direction direction() {
        return direction;
    }

    public Axis axis() {
        return axis;
    }

    public Vector3f vec() {
        return new Vector3f(vec);
    }

    public Vector3f spaceVec() {
        return new Vector3f(spaceVec);
    }

    public byte mask() {
        return mask;
    }
}
