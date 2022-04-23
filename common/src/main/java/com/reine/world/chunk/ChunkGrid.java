package com.reine.world.chunk;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChunkGrid {
    private final Map<ChunkPosition, Chunk> chunkMap = new HashMap<>();

    @Nullable
    public Chunk getChunk(int x, int y, int z) {
        return chunkMap.get(ChunkPosition.fromGlobal(x, y, z));
    }

    public void setChunk(int x, int y, int z, Chunk chunk) {
        chunkMap.put(ChunkPosition.fromGlobal(x, y, z), chunk);
    }

    public void setBlockId(int x, int y, int z, int blockId) {
        ChunkPosition pos = ChunkPosition.fromGlobal(x, y, z);

        Chunk chunk = chunkMap.get(pos);
        if (chunk == null || chunk.isEmpty()) {
            chunkMap.put(pos, chunk = new Chunk(pos));
        }

        chunk.setBlockId(
                x & IChunk.CHUNK_COORDINATE_MASK,
                y & IChunk.CHUNK_COORDINATE_MASK,
                z & IChunk.CHUNK_COORDINATE_MASK,
                blockId
        );
    }

    public int getBlockId(int x, int y, int z) {
        ChunkPosition key = ChunkPosition.fromGlobal(x, y, z);

        Chunk simpleChunk = chunkMap.get(key);
        if (simpleChunk == null) {
            return 0;
        }

        return simpleChunk.getBlockId(
                x & IChunk.CHUNK_COORDINATE_MASK,
                y & IChunk.CHUNK_COORDINATE_MASK,
                z & IChunk.CHUNK_COORDINATE_MASK
        );
    }

    public Map<ChunkPosition, Chunk> getChunkMap() {
        return Collections.unmodifiableMap(this.chunkMap);
    }

    public Collection<Chunk> loadedChunks() {
        return chunkMap.values();
    }
}
