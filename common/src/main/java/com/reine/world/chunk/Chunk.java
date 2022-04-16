package com.reine.world.chunk;

public interface Chunk {
    int CHUNK_WIDTH = 16;
    int CHUNK_HEIGHT = 16;
    int CHUNK_LENGTH = 16;
    int CHUNK_SIZE = CHUNK_WIDTH * CHUNK_HEIGHT * CHUNK_LENGTH;
    int CHUNK_COORDINATE_BITS = 4;
    int CHUNK_COORDINATE_MASK = (1 << CHUNK_COORDINATE_BITS) - 1;

    boolean isEmpty();

    static int idx(int x, int y, int z) {
        return x | (y << CHUNK_COORDINATE_BITS) | (z << CHUNK_COORDINATE_BITS * 2);
    }
}
