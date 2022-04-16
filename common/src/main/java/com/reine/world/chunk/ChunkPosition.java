package com.reine.world.chunk;

public record ChunkPosition(int x, int y, int z) {
    public static ChunkPosition fromGlobal(int x, int y, int z) {
        return new ChunkPosition(
                x >> Chunk.CHUNK_COORDINATE_BITS,
                y >> Chunk.CHUNK_COORDINATE_BITS,
                z >> Chunk.CHUNK_COORDINATE_BITS
        );
    }
}
