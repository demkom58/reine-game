package com.reine.client.render.chunk.mesh;

import com.crown.graphic.unit.ComposedMesh;
import com.crown.graphic.unit.Mesh;
import com.crown.graphic.util.Destroyable;
import com.reine.block.BlockLayer;

import java.util.EnumMap;

public record RenderChunk(int x, int y, int z, EnumMap<BlockLayer, ComposedMesh> passes) implements Destroyable {
    @Override
    public void destroy() {
        passes.values().forEach(Mesh::destroy);
    }
}
