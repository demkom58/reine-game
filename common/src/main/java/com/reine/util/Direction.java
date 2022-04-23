package com.reine.util;

public enum Direction {
    POSITIVE(1),
    NEGATIVE(-1);

    private final int sign;

    Direction(int sign) {
        this.sign = sign;
    }

    public int getSign() {
        return sign;
    }
}
