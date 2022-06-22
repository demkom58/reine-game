package com.reine.util;

import org.joml.Vector3b;
import org.joml.Vector3f;
import org.joml.Vector3i;

public enum WorldSide {
    DOWN(-1, 1, Direction.NEGATIVE, Axis.Y, new Vector3b(0, -1, 0), new Vector3b(1, 0, 1)),
    UP(-1, 0, Direction.POSITIVE, Axis.Y, new Vector3b(0, 1, 0), new Vector3b(1, 0, 1)),
    NORTH(2, 3, Direction.NEGATIVE, Axis.Z, new Vector3b(0, 0, -1), new Vector3b(1, 1, 0)),
    SOUTH(0, 2, Direction.POSITIVE, Axis.Z, new Vector3b(0, 0, 1), new Vector3b(1, 1, 0)),
    WEST(1, 5, Direction.NEGATIVE, Axis.X, new Vector3b(-1, 0, 0), new Vector3b(0, 1, 1)),
    EAST(3, 4, Direction.POSITIVE, Axis.X, new Vector3b(1, 0, 0), new Vector3b(0, 1, 1));

    private static final WorldSide[] vals = values();

    private final int horizontalOrder;
    private final int oppositeSideIdx;
    private final Direction direction;
    private final Axis axis;
    private final Vector3b vec;
    private final Vector3b spaceVec;

    private final byte mask;

    WorldSide(int horizontalOrder, int oppositeSideIdx, Direction direction, Axis axis, Vector3b vec, Vector3b spaceVec) {
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

    public Vector3b vec() {
        return new Vector3b(vec);
    }

    public Vector3i vecInt() {
        return vec.toInt();
    }

    public Vector3b spaceVec() {
        return new Vector3b(spaceVec);
    }

    public Vector3i spaceVecInt() {
        return spaceVec.toInt();
    }

    public byte mask() {
        return mask;
    }
}
