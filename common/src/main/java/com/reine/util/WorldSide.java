package com.reine.util;

import org.joml.Vector3i;

public enum WorldSide {
    DOWN(-1, 1, Direction.NEGATIVE, Axis.Y, new Vector3i(0, -1, 0)),
    UP(-1, 0, Direction.POSITIVE, Axis.Y, new Vector3i(0, 1, 0)),
    NORTH(2, 3, Direction.NEGATIVE, Axis.Z, new Vector3i(0, 0, -1)),
    SOUTH(0, 2, Direction.POSITIVE, Axis.Z, new Vector3i(0, 0, 1)),
    WEST(1, 5, Direction.NEGATIVE, Axis.X, new Vector3i(-1, 0, 0)),
    EAST(3, 4, Direction.POSITIVE, Axis.X, new Vector3i(1, 0, 0));

    private static final WorldSide[] vals = values();

    private final int horizontalOrder;
    private final int oppositeSideIdx;
    private final Direction direction;
    private final Axis axis;
    private final Vector3i vector;

    private final byte mask;

    WorldSide(int horizontalOrder, int oppositeSideIdx, Direction direction, Axis axis, Vector3i vector) {
        this.horizontalOrder = horizontalOrder;
        this.oppositeSideIdx = oppositeSideIdx;
        this.direction = direction;
        this.axis = axis;
        this.vector = vector;
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

    public Vector3i facingVector() {
        return new Vector3i(vector);
    }

    public byte mask() {
        return mask;
    }
}
