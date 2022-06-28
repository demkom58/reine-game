package com.crown.graphic.gl.buffer;

public record GlBufferSegment(GlBufferHeap heap, int start, int len) {
    public void delete() {
        this.heap.free(this);
    }
}
