package com.reine.client.render.chunk;

import com.crown.graphic.unit.ComposedMesh;
import com.crown.graphic.unit.Mesh;
import com.crown.graphic.unit.SplitMesh;
import com.crown.graphic.util.Destroyable;

import java.util.EnumMap;

public record RenderChunk(int x, int y, int z, EnumMap<RenderPass, ComposedMesh> passes) implements Destroyable {
    @Override
    public void destroy() {
        passes.values().forEach(Mesh::destroy);
    }
}
