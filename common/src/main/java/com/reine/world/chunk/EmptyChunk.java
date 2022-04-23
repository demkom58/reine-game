package com.reine.world.chunk;

public record EmptyChunk(int x, int y, int z) implements IChunk {

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
        return 0;
    }

    @Override
    public int getBlockId(int idx) {
        return 0;
    }

    @Override
    public void setBlockId(int x, int y, int z, int blockId) {
        if (blockId != 0) {
            throw new UnsupportedOperationException("Block can't be placed in empty chunk!");
        }
    }

    @Override
    public void setBlockId(int idx, int blockId) {
        if (blockId != 0) {
            throw new UnsupportedOperationException("Block can't be placed in empty chunk!");
        }
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
