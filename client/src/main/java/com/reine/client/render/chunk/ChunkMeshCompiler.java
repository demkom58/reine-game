package com.reine.client.render.chunk;

import com.crown.graphic.unit.ComposedMesh;
import com.crown.graphic.unit.SplitMesh;

import java.util.List;

public interface ChunkMeshCompiler {
    ComposedMesh compile(List<ChunkQuad> quads);
}
