package com.reine.client.render.chunk;

import com.crown.graphic.unit.Mesh;
import com.reine.util.WorldSide;
import org.joml.Vector3f;

public interface ChunkMeshConstructor {
    Mesh construct(Vector3f start, Vector3f end, WorldSide side, int blockId);
}
