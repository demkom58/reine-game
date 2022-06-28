package com.crown.graphic.unit;

import com.crown.graphic.gl.array.GlVertexArray;
import com.crown.graphic.gl.attribute.GlVertexFormat;
import com.crown.graphic.gl.buffer.*;
import com.crown.graphic.util.ListHandler;

import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class BatchMesh implements Mesh {
    private final int mode;
    private final GlVertexFormat<?> format;
    private final GlFixedHeapMap heapMap;
    private final GlDrawCmdSet ibo;
    private final GlDrawCmdSet.Command[] commands;
    private GlVertexArray vao;

    public BatchMesh(int mode,
                     GlVertexFormat<?> format,
                     GlFixedHeapMap heapMap,
                     GlDrawCmdSet ibo) {
        this.mode = mode;

        this.format = format;
        this.heapMap = heapMap;
        this.ibo = ibo;
        this.commands = new GlDrawCmdSet.Command[heapMap.size()];

        recreateVao();
    }

    private void recreateVao() {
        if (vao != null) {
            vao.destroy();
        }

        vao = new GlVertexArray();
        vao.bind();

        GlBuffer vbo = heapMap.getBuffer();
        vbo.bind(GL_ARRAY_BUFFER);

        format.bindVertexAttributes();
        format.enableVertexAttributes();

        vbo.unbind(GL_ARRAY_BUFFER);
        vao.unbind();
    }

    public void update(int index, VerticesData data) {
        update(index, data, null);
    }

    public void update(int index, VerticesData data, ListHandler<Integer> synchronizer) {
        final GlBuffer beforeUpdateBuffer = heapMap.getBuffer();

        GlBufferSegment segment = heapMap.update(index, data);
        // means that buffer in heap was reallocated,
        // so VAO should have new VBO address
        if (heapMap.getBuffer() != beforeUpdateBuffer) {
            recreateVao();
        }

        GlDrawCmdSet.Command previous = commands[index];
        if (previous != null) {
            int removed = ibo.remove(previous);
            if (removed != -1 && synchronizer != null) {
                synchronizer.removed(removed, index);
            }
        }

        if (data == null) {
            commands[index] = null;
        } else {
            int start = segment.start() / format.getStride();
            int end = data.vertexCount();

            var command = new GlDrawCmdSet.Command(start, end, 0, 1);
            commands[index] = command;

            int drawId = ibo.add(command);
            if (synchronizer != null) {
                synchronizer.added(drawId, index);
            }
        }
    }

    @Override
    public void bind() {
        ibo.bind();
        vao.bind();
    }

    @Override
    public void unbind() {
        vao.unbind();
        ibo.unbind();
    }

    @Override
    public void draw() {
        glMultiDrawArraysIndirect(mode, 0L, ibo.size(), 16);
    }

    @Override
    public void destroy() {
        heapMap.unbind();
        heapMap.getBuffer().unbind(GL_VERTEX_ARRAY);
        vao.unbind();
        ibo.unbind();

        heapMap.destroy();
        vao.destroy();
        ibo.destroy();
    }

    public static BatchMesh of(int mode, GlVertexFormat<?> fmt, int size, int verticesAvgCount) {
        GlFixedHeapMap map = GlFixedHeapMap.of(size, verticesAvgCount, fmt.getStride());
        return new BatchMesh(mode, fmt, map, new GlDrawCmdSet(GL_DYNAMIC_DRAW, size));
    }

    public static BatchMesh of(int mode, List<VerticesData> data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Mesh should have objects to render!");
        }

        GlVertexFormat<?> fmt = data.get(0).format();
        GlFixedHeapMap map = GlFixedHeapMap.of(data);

        GlDrawCmdSet ibo = new GlDrawCmdSet(GL_DYNAMIC_DRAW, data.size());
        for (int i = 0; i < map.size(); i++) {
            GlBufferSegment segment = map.get(i);
            VerticesData verticesData = data.get(i);

            int start = segment.start() / fmt.getStride();
            int vertexCount = verticesData.vertexCount();

            ibo.add(new GlDrawCmdSet.Command(start, vertexCount, 0, 1));
        }

        return new BatchMesh(mode, fmt, map, ibo);
    }
}
