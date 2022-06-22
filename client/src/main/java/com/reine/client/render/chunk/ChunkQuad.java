package com.reine.client.render.chunk;

import com.reine.util.WorldSide;
import org.joml.Vector3f;
import org.joml.Vector3i;

public record ChunkQuad(Vector3i start, Vector3i end, WorldSide side, int blockId) {
}
