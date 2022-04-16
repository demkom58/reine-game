package com.reine.world.chunk;

import com.google.common.base.Preconditions;

public class SimpleChunk implements Chunk {
    private final int x;
    private final int y;
    private final int z;

    private final int[] blocks;

    public SimpleChunk(int x, int y, int z) {
        this(x, y, z, new int[CHUNK_SIZE]);
    }

    public SimpleChunk(int x, int y, int z, int[] blocks) {
        this.x = x;
        this.y = y;
        this.z = z;
        Preconditions.checkArgument(blocks.length == CHUNK_SIZE, "Chunk block array size is not " + CHUNK_SIZE);
        this.blocks = blocks;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getBlockId(int x, int y, int z) {
        return blocks[Chunk.idx(x, y, z)];
    }

    public void setBlockId(int x, int y, int z, int blockId) {
        blocks[Chunk.idx(x, y, z)] = blockId;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
