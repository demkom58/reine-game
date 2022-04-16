package com.reine.world.chunk;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChunkGrid {
    private final Map<ChunkPosition, SimpleChunk> chunkMap = new HashMap<>();

    public SimpleChunk getChunk(int x, int y, int z) {
        return chunkMap.get(ChunkPosition.fromGlobal(x, y, z));
    }

    public void setChunk(int x, int y, int z, SimpleChunk chunk) {
        chunkMap.put(ChunkPosition.fromGlobal(x, y, z), chunk);
    }

    public void setBlockId(int x, int y, int z, int blockId) {
        ChunkPosition key = ChunkPosition.fromGlobal(x, y, z);

        SimpleChunk simpleChunk = chunkMap.get(key);
        if (simpleChunk == null) {
            chunkMap.put(key, simpleChunk = new SimpleChunk(key.x(), key.y(), key.z()));
        }

        simpleChunk.setBlockId(
                x & Chunk.CHUNK_COORDINATE_MASK,
                y & Chunk.CHUNK_COORDINATE_MASK,
                z & Chunk.CHUNK_COORDINATE_MASK,
                blockId
        );
    }

    public int getBlockId(int x, int y, int z) {
        ChunkPosition key = ChunkPosition.fromGlobal(x, y, z);

        SimpleChunk simpleChunk = chunkMap.get(key);
        if (simpleChunk == null) {
            return 0;
        }

        return simpleChunk.getBlockId(
                x & Chunk.CHUNK_COORDINATE_MASK,
                y & Chunk.CHUNK_COORDINATE_MASK,
                z & Chunk.CHUNK_COORDINATE_MASK
        );
    }

    public Collection<SimpleChunk> loadedChunks() {
        return chunkMap.values();
    }
}
