package com.reine.world.chunk;

import com.reine.block.Block;
import org.joml.Vector3i;

public interface IChunk {
    int CHUNK_WIDTH = 16;
    int CHUNK_HEIGHT = 16;
    int CHUNK_LENGTH = 16;
    int CHUNK_SIZE = CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_LENGTH;
    int CHUNK_COORDINATE_BITS = 4;
    int CHUNK_COORDINATE_MASK = (1 << CHUNK_COORDINATE_BITS) - 1;

    int getX();

    int getY();

    int getZ();

    int getBlockId(int x, int y, int z);

    int getBlockId(int idx);

    void setBlockId(int x, int y, int z, int blockId);

    void setBlockId(int idx, int blockId);

    boolean isEmpty();

    static int idx(int x, int y, int z) {
        return x | (y << CHUNK_COORDINATE_BITS) | (z << CHUNK_COORDINATE_BITS * 2);
    }
}
