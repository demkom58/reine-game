package com.reine.client.render.chunk;

import com.crown.graphic.unit.Mesh;
import com.crown.graphic.util.Destroyable;

import java.util.EnumMap;

public record RenderChunk(int x, int y, int z, EnumMap<RenderPass, Mesh> passes) implements Destroyable {
    @Override
    public void destroy() {
        passes.values().forEach(Mesh::destroy);
    }
}
