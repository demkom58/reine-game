package com.reine.world.chunk;

import com.google.common.base.Preconditions;

public class Chunk implements IChunk {
    private final int x;
    private final int y;
    private final int z;

    private final int[] blocks;

    public Chunk(ChunkPosition position) {
        this(position.x(), position.y(), position.z());
    }

    public Chunk(int x, int y, int z) {
        this(x, y, z, new int[CHUNK_SIZE]);
    }

    public Chunk(int x, int y, int z, int[] blocks) {
        this.x = x;
        this.y = y;
        this.z = z;
        Preconditions.checkArgument(blocks.length == CHUNK_SIZE, "IChunk block array size is not " + CHUNK_SIZE);
        this.blocks = blocks;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return blocks[IChunk.idx(x, y, z)];
    }

    @Override
    public int getBlockId(int idx) {
        return blocks[idx];
    }

    @Override
    public void setBlockId(int x, int y, int z, int blockId) {
        blocks[IChunk.idx(x, y, z)] = blockId;
    }

    @Override
    public void setBlockId(int idx, int blockId) {
        blocks[idx] = blockId;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
