package com.crown.graphic.gl.buffer;

import com.crown.graphic.util.Destroyable;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import static org.lwjgl.opengl.GL31.*;

import java.nio.*;
import java.util.Set;

public class GlBufferHeap implements Destroyable {
    private final int resizeIncrement;

    private final Set<GlBufferSegment> freeRegions = new ObjectLinkedOpenHashSet<>();

    private GlBuffer vertexBuffer;
    private boolean isBufferBound;

    private int position;
    private int capacity;
    private int allocCount;

    public GlBufferHeap(int initialSize, int resizeIncrement) {
        this.vertexBuffer = this.createBuffer();
        this.vertexBuffer.bind(GL_COPY_WRITE_BUFFER);
        this.vertexBuffer.allocate(GL_COPY_WRITE_BUFFER, initialSize);
        this.vertexBuffer.unbind(GL_COPY_WRITE_BUFFER);

        this.resizeIncrement = resizeIncrement;
        this.capacity = initialSize;
    }

    private void resize(int size) {
        GlBuffer src = this.vertexBuffer;
        src.unbind(GL_COPY_WRITE_BUFFER);

        GlBuffer dst = this.createBuffer();

        GlBuffer.copy(src, dst, 0, 0, this.capacity, size);
        src.destroy();

        dst.bind(GL_COPY_WRITE_BUFFER);

        this.vertexBuffer = dst;
        this.capacity = size;
    }

    private GlBuffer createBuffer() {
        return new GlMutableBuffer(GL_DYNAMIC_DRAW);
    }

    public void bind() {
        this.vertexBuffer.bind(GL_COPY_WRITE_BUFFER);
        this.isBufferBound = true;
    }

    public void ensureCapacity(int len) {
        if (this.position + len >= this.capacity) {
            this.resize(this.getNextSize(len));
        }
    }

    public GlBufferSegment copy(int readTarget, int offset, int len) {
        this.checkBufferBound();
        this.ensureCapacity(len);

        GlBufferSegment segment = this.alloc(len);
        glCopyBufferSubData(readTarget, GL_COPY_WRITE_BUFFER, offset, segment.getStart(), len);

        return segment;
    }

    public GlBufferSegment upload(VerticesData vertices) {
        return this.upload(vertices.buffer());
    }

    public GlBufferSegment upload(ByteBuffer buf) {
        int len = buf.remaining();

        this.checkBufferBound();
        this.ensureCapacity(len);

        GlBufferSegment segment = this.alloc(len);
        glBufferSubData(GL_COPY_WRITE_BUFFER, segment.getStart(), buf);

        return segment;
    }

    public void unbind() {
        this.checkBufferBound();

        this.vertexBuffer.unbind(GL_COPY_WRITE_BUFFER);
        this.isBufferBound = false;
    }

    private int getNextSize(int len) {
        return Math.max(this.capacity + this.resizeIncrement, this.capacity + len);
    }

    public void free(GlBufferSegment segment) {
        if (!this.freeRegions.add(segment)) {
            throw new IllegalArgumentException("Segment already freed");
        }

        this.allocCount--;
    }

    private GlBufferSegment alloc(int len) {
        GlBufferSegment segment = this.allocReuse(len);

        if (segment == null) {
            segment = new GlBufferSegment(this, this.position, len);

            this.position += len;
        }

        this.allocCount++;

        return segment;
    }

    private GlBufferSegment allocReuse(int len) {
        GlBufferSegment bestSegment = null;

        for (GlBufferSegment segment : this.freeRegions) {
            if (segment.getLength() < len) {
                continue;
            }

            if (bestSegment == null || bestSegment.getLength() > segment.getLength()) {
                bestSegment = segment;
            }
        }

        if (bestSegment == null) {
            return null;
        }

        this.freeRegions.remove(bestSegment);

        int excess = bestSegment.getLength() - len;

        if (excess > 0) {
            this.freeRegions.add(new GlBufferSegment(this, bestSegment.getStart() + len, excess));
        }

        return new GlBufferSegment(this, bestSegment.getStart(), len);
    }

    @Override
    public void destroy() {
        this.vertexBuffer.destroy();
    }

    public boolean isEmpty() {
        return this.allocCount <= 0;
    }

    public GlBuffer getBuffer() {
        return this.vertexBuffer;
    }

    private void checkBufferBound() {
        if (!this.isBufferBound) {
            throw new IllegalStateException("Buffer is not bound");
        }
    }
}
