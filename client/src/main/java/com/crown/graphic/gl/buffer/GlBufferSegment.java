package com.crown.graphic.gl.buffer;

public class GlBufferSegment {
    private final GlBufferHeap arena;
    private final int start;
    private final int len;

    GlBufferSegment(GlBufferHeap arena, int start, int len) {
        this.arena = arena;
        this.start = start;
        this.len = len;
    }

    public int getStart() {
        return this.start;
    }

    public int getLength() {
        return this.len;
    }

    public GlBufferHeap getArena() {
        return this.arena;
    }

    public void delete() {
        this.arena.free(this);
    }
}
