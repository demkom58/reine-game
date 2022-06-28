package com.reine.client.render.chunk.mesh;

import com.crown.graphic.gl.buffer.VerticesData;
import com.crown.graphic.util.Destroyable;
import com.reine.block.BlockLayer;
import org.lwjgl.system.MemoryUtil;

import java.util.EnumMap;

public record LayeredVertices(EnumMap<BlockLayer, VerticesData> vertices) implements Destroyable {
    public static final LayeredVertices EMPTY = new LayeredVertices(new EnumMap<>(BlockLayer.class));

    @Override
    public void destroy() {
        vertices.values().forEach(v -> MemoryUtil.memFree(v.buffer()));
    }
}
