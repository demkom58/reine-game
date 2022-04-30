package com.reine.world.chunk;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChunkGrid {
    private final Map<ChunkPosition, IChunk> chunkMap = new HashMap<>();

    @Nullable
    public IChunk getChunk(int x, int y, int z) {
        return chunkMap.get(ChunkPosition.fromGlobal(x, y, z));
    }

    public void setChunk(int x, int y, int z, IChunk chunk) {
        chunkMap.put(ChunkPosition.fromGlobal(x, y, z), chunk);
    }

    public void setBlockId(int x, int y, int z, int blockId) {
        ChunkPosition pos = ChunkPosition.fromGlobal(x, y, z);

        IChunk chunk = chunkMap.get(pos);
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

        IChunk chunk = chunkMap.get(key);
        if (chunk == null || chunk.isEmpty()) {
            return 0;
        }

        return chunk.getBlockId(
                x & IChunk.CHUNK_COORDINATE_MASK,
                y & IChunk.CHUNK_COORDINATE_MASK,
                z & IChunk.CHUNK_COORDINATE_MASK
        );
    }

    public Map<ChunkPosition, IChunk> getChunkMap() {
        return Collections.unmodifiableMap(this.chunkMap);
    }

    public Collection<IChunk> loadedChunks() {
        return chunkMap.values();
    }
}
