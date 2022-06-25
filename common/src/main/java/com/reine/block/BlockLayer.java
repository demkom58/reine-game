package com.reine.block;

public enum BlockLayer {
    SOLID(false),
    OPAQUE(false),
    TRANSPARENT(true);

    public final boolean hasAlpha;

    BlockLayer(boolean hasAlpha) {
        this.hasAlpha = hasAlpha;
    }

}
