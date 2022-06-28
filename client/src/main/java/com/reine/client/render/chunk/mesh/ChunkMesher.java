package com.reine.client.render.chunk.mesh;

import com.crown.graphic.gl.buffer.VerticesData;
import com.crown.graphic.unit.ComposedMesh;
import com.reine.client.TextureManager;
import com.reine.client.render.chunk.ChunkFormat;
import com.reine.client.render.chunk.util.FaceChunk;
import com.reine.client.render.chunk.util.ChunkQuad;
import com.reine.block.BlockLayer;
import com.reine.util.Direction;
import com.reine.util.WorldSide;
import com.reine.world.chunk.IChunk;
import org.joml.Vector3b;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static com.reine.world.chunk.IChunk.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class ChunkMesher {
    public static final int TRIANGLE_VERTICES = 3;
    public static final int QUAD_TRIANGLES = 2;

    private final TextureManager textureManager;

    public ChunkMesher(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public LayeredVertices mesh(IChunk chunk, FaceChunk faceChunk) {
        if (chunk.isEmpty()) {
            return LayeredVertices.EMPTY;
        }

        final EnumMap<BlockLayer, VerticesData> meshes = new EnumMap<>(BlockLayer.class);
        for (BlockLayer pass : BlockLayer.values()) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                List<ChunkQuad> quads = greedyQuads(stack, chunk, faceChunk.getBuffer(pass));
                VerticesData mesh = compileMesh(quads);
                if (mesh != null) {
                    meshes.put(pass, mesh);
                }
            }
        }

        return new LayeredVertices(meshes);
    }

    private List<ChunkQuad> simpleQuads(MemoryStack stack, IChunk chunk, ByteBuffer culledFaces) {
        final List<ChunkQuad> quads = new ArrayList<>();

        for (WorldSide side : WorldSide.values()) {
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                for (int y = 0; y < CHUNK_HEIGHT; y++) {
                    for (int z = 0; z < CHUNK_LENGTH; z++) {
                        final int index = idx(x, y, z);
                        final int blockId = chunk.getBlockId(index);
                        if (blockId <= 0 || !isSideVisible(culledFaces, index, side)) {
                            continue;
                        }

                        Vector3b start = side.direction() == Direction.POSITIVE ? side.vec() : new Vector3b();
                        start.add(x, y, z);

                        Vector3b end = new Vector3b(start);
                        end.add(side.spaceVec());

                        final ChunkQuad quad = new ChunkQuad(start, end, side, blockId);

                        quads.add(quad);
                    }
                }
            }
        }

        return quads;
    }

    private List<ChunkQuad> greedyQuads(MemoryStack stack, IChunk chunk, ByteBuffer culledFaces) {
        final ByteBuffer mask = stack.calloc(CHUNK_SIZE);
        final List<ChunkQuad> meshes = new ArrayList<>();

        for (WorldSide side : WorldSide.values()) {
            for (int x = 0; x < CHUNK_WIDTH; x++) {
                for (int y = 0; y < CHUNK_HEIGHT; y++) {
                    for (int z = 0; z < CHUNK_LENGTH; z++) {

                        final int index = idx(x, y, z);
                        int blockId = chunk.getBlockId(index);

                        if (blockId <= 0
                                || isSideVisible(mask, index, side)
                                || !isSideVisible(culledFaces, index, side)) {
                            continue;
                        }

                        switch (side) {
                            case WEST, EAST -> {
                                int width = 0;
                                int height = 0;

                                loop:
                                for (int iY = y; iY < CHUNK_HEIGHT; iY++) {
                                    if (iY == y) {
                                        for (int iZ = z; iZ < CHUNK_LENGTH; iZ++) {
                                            int iterIdx = idx(x, iY, iZ);

                                            if (!isSideVisible(mask, iterIdx, side)
                                                    && isSideVisible(culledFaces, iterIdx, side)
                                                    && chunk.getBlockId(iterIdx) == blockId) {
                                                width++;
                                            } else break;
                                        }
                                    } else {
                                        for (int iZ = 0; iZ < width; iZ++) {
                                            int iterIndex = idx(x, iY, z + iZ);
                                            if (isSideVisible(mask, iterIndex, side)
                                                    || !isSideVisible(culledFaces, iterIndex, side)
                                                    || chunk.getBlockId(iterIndex) != blockId) {
                                                break loop;
                                            }
                                        }
                                    }
                                    height++;
                                }

                                for (int iY = 0; iY < height; iY++) {
                                    for (int iZ = 0; iZ < width; iZ++) {
                                        int idx = idx(x, y + iY, z + iZ);
                                        mask.put(idx, (byte) (mask.get(index) | side.mask()));
                                    }
                                }

                                int sideOffset = side.direction() == Direction.POSITIVE ? 1 : 0;
                                meshes.add(new ChunkQuad(
                                        new Vector3b(x + sideOffset, y, z),
                                        new Vector3b(x + sideOffset, y + height, z + width),
                                        side,
                                        blockId
                                ));
                            }
                            case DOWN, UP -> {
                                int width = 0;
                                int height = 0;

                                loop:
                                for (int iX = x; iX < CHUNK_WIDTH; iX++) {
                                    if (iX == x) {
                                        for (int iZ = z; iZ < CHUNK_LENGTH; iZ++) {
                                            int iterIdx = idx(iX, y, iZ);
                                            if (!isSideVisible(mask, iterIdx, side)
                                                    && isSideVisible(culledFaces, iterIdx, side)
                                                    && chunk.getBlockId(iterIdx) == blockId) {
                                                height++;
                                            } else break;
                                        }
                                    } else {
                                        for (int iZ = 0; iZ < height; iZ++) {
                                            int iterIdx = idx(iX, y, z + iZ);
                                            if (isSideVisible(mask, iterIdx, side)
                                                    || !isSideVisible(culledFaces, iterIdx, side)
                                                    || chunk.getBlockId(iterIdx) != blockId) {
                                                break loop;
                                            }
                                        }
                                    }
                                    width++;
                                }

                                for (int iX = 0; iX < width; iX++) {
                                    for (int iZ = 0; iZ < height; iZ++) {
                                        int idx = idx(x + iX, y, z + iZ);
                                        mask.put(idx, (byte) (mask.get(index) | side.mask()));
                                    }
                                }

                                int sideOffset = side.direction() == Direction.POSITIVE ? 1 : 0;
                                meshes.add(new ChunkQuad(
                                        new Vector3b(x, y + sideOffset, z),
                                        new Vector3b(x + width, y + sideOffset, z + height),
                                        side,
                                        blockId
                                ));
                            }
                            case NORTH, SOUTH -> {
                                int height = 0;
                                int width = 0;

                                loop:
                                for (int iX = x; iX < CHUNK_WIDTH; iX++) {
                                    if (iX == x) {
                                        for (int iY = y; iY < CHUNK_HEIGHT; iY++) {
                                            int iterIdx = idx(iX, iY, z);
                                            if (!isSideVisible(mask, iterIdx, side)
                                                    && isSideVisible(culledFaces, iterIdx, side)
                                                    && chunk.getBlockId(iterIdx) == blockId) {
                                                height++;
                                            } else break;
                                        }
                                    } else {
                                        for (int iY = 0; iY < height; iY++) {
                                            int iterIdx = idx(iX, y + iY, z);
                                            if (isSideVisible(mask, iterIdx, side)
                                                    || !isSideVisible(culledFaces, iterIdx, side)
                                                    || chunk.getBlockId(iterIdx) != blockId) {
                                                break loop;
                                            }
                                        }
                                    }
                                    width++;
                                }

                                for (int iX = 0; iX < width; iX++) {
                                    for (int iY = 0; iY < height; iY++) {
                                        int idx = idx(x + iX, y + iY, z);
                                        mask.put(idx, (byte) (mask.get(index) | side.mask()));
                                    }
                                }

                                int sideOffset = side.direction() == Direction.POSITIVE ? 1 : 0;
                                meshes.add(new ChunkQuad(
                                        new Vector3b(x, y, z + sideOffset),
                                        new Vector3b(x + width, y + height, z + sideOffset),
                                        side,
                                        blockId
                                ));
                            }
                        }
                    }
                }
            }
        }

        return meshes;
    }

    public VerticesData compileMesh(List<ChunkQuad> quads) {
        final int quadsCount = quads.size();
        if (quadsCount == 0) {
            return null;
        }

        final int verticesCount = quadsCount * QUAD_TRIANGLES * TRIANGLE_VERTICES;
        final int bytesCount = verticesCount * ChunkFormat.CHUNK_FORMAT.getStride();

        final ByteBuffer vertexData = MemoryUtil.memAlloc(bytesCount);
        ChunkFormat.write(textureManager, quads, vertexData);
        vertexData.flip();

        return new VerticesData(ChunkFormat.CHUNK_FORMAT, vertexData);
    }

    private static boolean isSideVisible(ByteBuffer masks, int idx, WorldSide side) {
        final byte mask = side.mask();
        return (masks.get(idx) & mask) == mask;
    }

    private static int idx(int x, int y, int z) {
        return IChunk.idx(x, y, z);
    }
}
