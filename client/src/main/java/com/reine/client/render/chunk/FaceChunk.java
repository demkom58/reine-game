package com.reine.client.render.chunk;

import com.crown.graphic.util.Destroyable;
import com.reine.util.WorldSide;
import com.reine.block.Block;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.IChunk;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

public class FaceChunk implements Destroyable {
    private final EnumMap<RenderPass, ByteBuffer> passBuffers = new EnumMap<>(Map.of(
            RenderPass.SOLID, MemoryUtil.memCalloc(IChunk.CHUNK_SIZE),
            RenderPass.TRANSPARENT, MemoryUtil.memCalloc(IChunk.CHUNK_SIZE)
    ));

    public void singleUpdate(ChunkGrid grid, IChunk chunk, int x, int y, int z) {
        for (RenderPass pass : RenderPass.values()) {
            updateBlock(pass, grid, chunk, x, y, z, passBuffers.get(pass));
        }
    }

    public void update(ChunkGrid grid, IChunk chunk, int x, int y, int z) {
        for (RenderPass pass : RenderPass.values()) {
            final ByteBuffer passBuffer = passBuffers.get(pass);
            updateBlock(pass, grid, chunk, x, y, z, passBuffer);
            updateBlock(pass, grid, chunk, x + 1, y, z, passBuffer);
            updateBlock(pass, grid, chunk, x - 1, y, z, passBuffer);
            updateBlock(pass, grid, chunk, x, y + 1, z, passBuffer);
            updateBlock(pass, grid, chunk, x, y - 1, z, passBuffer);
            updateBlock(pass, grid, chunk, x, y, z + 1, passBuffer);
            updateBlock(pass, grid, chunk, x, y, z - 1, passBuffer);
        }
    }

    public ByteBuffer getBuffer(RenderPass pass) {
        return passBuffers.get(pass);
    }

    @Override
    public void destroy() {
        passBuffers.values().forEach(MemoryUtil::memFree);
    }

    public static FaceChunk build(ChunkGrid grid, IChunk chunk) {
        final FaceChunk faceChunk = new FaceChunk();

        for (RenderPass pass : RenderPass.values()) {
            final ByteBuffer buffer = faceChunk.getBuffer(pass);

            for (int x = 0; x < IChunk.CHUNK_WIDTH; x++) {
                for (int y = 0; y < IChunk.CHUNK_HEIGHT; y++) {
                    for (int z = 0; z < IChunk.CHUNK_LENGTH; z++) {
                        updateBlock(pass, grid, chunk, x, y, z, buffer);
                    }
                }
            }
        }

        return faceChunk;
    }

    private static void updateBlock(RenderPass pass, ChunkGrid grid, IChunk chunk, int x, int y, int z, ByteBuffer buffer) {
        int index = IChunk.idx(x, y, z);
        int blockId = chunk.getBlockId(index);
        if (blockId == 0) {
            buffer.put(index, (byte) 0);
            return;
        }

        final Block block = Block.byId(blockId);
        final boolean transparent = block.isTransparent();
        if ((pass == RenderPass.SOLID && !transparent) || (pass == RenderPass.TRANSPARENT && transparent)) {
            byte sidesMask = 0b000000;

            // fill visibility of all sides for current block
            for (WorldSide side : WorldSide.values()) {
                final Block neighbor = getNeighborBlock(grid, chunk, x, y, z, side);
                if (block != neighbor && neighbor.isTransparent()) {
                    sidesMask |= side.mask();
                }
            }

            // calc index of block and set calculated mask
            buffer.put(index, sidesMask);
        }
    }

    private static Block getNeighborBlock(ChunkGrid grid, IChunk chunk, int x, int y, int z, WorldSide side) {
        final Vector3f offset = side.vec();
        x += offset.x;
        y += offset.y;
        z += offset.z;

        if (x >= 0 && x < IChunk.CHUNK_WIDTH && y >= 0 && y < IChunk.CHUNK_HEIGHT && z >= 0 && z < IChunk.CHUNK_LENGTH) {
            return Block.byId(chunk.getBlockId(x, y, z));
        }

        final int globalX = chunk.getX() * IChunk.CHUNK_WIDTH + x;
        final int globalY = chunk.getY() * IChunk.CHUNK_HEIGHT + y;
        final int globalZ = chunk.getZ() * IChunk.CHUNK_LENGTH + z;
        final IChunk neighbor = grid.getChunk(
                globalX >> IChunk.CHUNK_COORDINATE_BITS,
                globalY >> IChunk.CHUNK_COORDINATE_BITS,
                globalZ >> IChunk.CHUNK_COORDINATE_BITS
        );

        if (neighbor == null || neighbor.isEmpty()) {
            return Block.AIR;
        }

        final int blockId = neighbor.getBlockId(
                globalX & IChunk.CHUNK_COORDINATE_MASK,
                globalY & IChunk.CHUNK_COORDINATE_MASK,
                globalZ & IChunk.CHUNK_COORDINATE_MASK
        );

        return Block.byId(blockId);
    }
}
