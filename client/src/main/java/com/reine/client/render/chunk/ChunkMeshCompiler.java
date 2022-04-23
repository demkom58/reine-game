package com.reine.client.render.chunk;

import com.crown.graphic.unit.Mesh;

import java.util.List;

public interface ChunkMeshCompiler {
    Mesh compile(List<ChunkQuad> quads);
}
