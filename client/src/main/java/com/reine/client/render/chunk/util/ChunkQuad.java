package com.reine.client.render.chunk.util;

import com.reine.util.WorldSide;
import org.joml.Vector3b;

public record ChunkQuad(Vector3b start, Vector3b end, WorldSide side, int blockId) {
}
