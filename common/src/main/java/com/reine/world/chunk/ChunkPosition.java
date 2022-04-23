package com.reine.world.chunk;

public record ChunkPosition(int x, int y, int z) {
    public static ChunkPosition fromChunk(IChunk chunk) {
        return new ChunkPosition(chunk.getX(), chunk.getY(), chunk.getZ());
    }

    public static ChunkPosition fromGlobal(int x, int y, int z) {
        return new ChunkPosition(
                x >> IChunk.CHUNK_COORDINATE_BITS,
                y >> IChunk.CHUNK_COORDINATE_BITS,
                z >> IChunk.CHUNK_COORDINATE_BITS
        );
    }
}
