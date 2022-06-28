package com.crown.graphic.gl.buffer;

import com.crown.graphic.gl.attribute.GlVertexFormat;
import com.crown.graphic.util.Destroyable;

import java.util.Comparator;
import java.util.List;

public class GlFixedHeapMap implements Destroyable {
    private final GlBufferSegment[] segments;
    private final GlBufferHeap vertexHeap;

    public GlFixedHeapMap(GlBufferSegment[] segments, GlBufferHeap heap) {
        this.segments = segments;
        this.vertexHeap = heap;
    }

    public GlBuffer getBuffer() {
        return vertexHeap.getBuffer();
    }

    public int size() {
        return segments.length;
    }

    public GlBufferSegment get(int index) {
        return segments[index];
    }

    public GlBufferSegment update(int index, VerticesData data) {
        vertexHeap.bind();

        GlBufferSegment segment = segments[index];
        if (segment != null) {
            vertexHeap.free(segment);
        }

        if (data != null) {
            segments[index] = segment = vertexHeap.upload(data);
        } else {
            segments[index] = segment = null;
        }

        vertexHeap.unbind();
        return segment;
    }

    public void unbind() {
        vertexHeap.unbind();
    }

    @Override
    public void destroy() {
        vertexHeap.unbind();
        vertexHeap.destroy();
    }

    public static GlFixedHeapMap of(int size, int verticesAvgCount, int stride) {
        GlBufferSegment[] segments = new GlBufferSegment[size];

        final int avgSize = stride * verticesAvgCount;

        int initialSize = avgSize * size;
        GlBufferHeap hbo = new GlBufferHeap(initialSize, (int) (initialSize * 0.5f));

        return new GlFixedHeapMap(segments, hbo);
    }

    public static GlFixedHeapMap of(GlBufferSegment[] segments, int verticesAvgCount, int stride) {
        final int avgSize = stride * verticesAvgCount;

        int initialSize = avgSize * segments.length;
        GlBufferHeap hbo = new GlBufferHeap(initialSize, (int) (initialSize * 0.5f));

        return new GlFixedHeapMap(segments, hbo);
    }

    public static GlFixedHeapMap of(List<VerticesData> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Map should have data!");
        }

        GlVertexFormat<?> fmt = data.get(0).format();
        float maxSize = data.stream()
                .max(Comparator.comparing(VerticesData::vertexCount))
                .get()
                .vertexCount() * fmt.getStride() * data.size();

        GlBufferSegment[] segments = new GlBufferSegment[data.size()];

        GlBufferHeap hbo = new GlBufferHeap((int) maxSize, (int) (maxSize * 0.5f));
        hbo.bind();
        for (int i = 0; i < data.size(); i++) {
            segments[i] = hbo.upload(data.get(i));
        }
        hbo.unbind();
        return new GlFixedHeapMap(segments, hbo);
    }
}
