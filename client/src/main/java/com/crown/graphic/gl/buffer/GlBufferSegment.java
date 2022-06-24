package com.crown.graphic.gl.buffer;

public class GlBufferSegment {
    private final GlBufferHeap heap;
    private final int start;
    private final int len;

    GlBufferSegment(GlBufferHeap heap, int start, int len) {
        this.heap = heap;
        this.start = start;
        this.len = len;
    }

    public int getStart() {
        return this.start;
    }

    public int getLength() {
        return this.len;
    }

    public GlBufferHeap getHeap() {
        return this.heap;
    }

    public void delete() {
        this.heap.free(this);
    }
}
