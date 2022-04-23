package com.reine.client.render.chunk;

import com.reine.util.WorldSide;
import org.joml.Vector3f;

public record ChunkQuad(Vector3f start, Vector3f end, WorldSide side, int blockId) {
}
