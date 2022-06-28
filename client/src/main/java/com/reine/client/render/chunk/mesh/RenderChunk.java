package com.reine.client.render.chunk.mesh;

import com.crown.graphic.gl.buffer.GlMutableBuffer;
import com.crown.graphic.gl.buffer.VerticesData;
import com.crown.graphic.unit.BatchMesh;
import com.crown.graphic.unit.Mesh;
import com.crown.graphic.util.Destroyable;
import com.reine.block.BlockLayer;
import com.reine.client.render.chunk.ChunkFormat;
import com.reine.world.chunk.ChunkPosition;
import com.reine.world.chunk.IChunk;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public record RenderChunk(Position pos,
                          EnumMap<BlockLayer, BatchMesh> passes,
                          EnumMap<BlockLayer, GlMutableBuffer> modelUniforms)
        implements Destroyable {
    public static final int CHUNKS_COUNT = 8;
    public static final int CHUNKS_GROUP_AXIS_SIZE = CHUNKS_COUNT * IChunk.CHUNK_WIDTH;
    public static final int RENDER_CHUNK_SIZE = CHUNKS_COUNT * CHUNKS_COUNT * CHUNKS_COUNT;
    public static final int CHUNK_COORDINATE_BITS = 3; // 8 (3 bits)
    public static final int CHUNK_COORDINATE_MASK = (1 << CHUNK_COORDINATE_BITS) - 1;

    public static final int UNIFORM_MATRIX_SIZE = (4 * 4) * Float.BYTES;
    public static final int UNIFORM_BUFFER_SIZE = RENDER_CHUNK_SIZE * UNIFORM_MATRIX_SIZE;

    @Override
    public void destroy() {
        passes.values().forEach(Destroyable::destroy);
        modelUniforms.values().forEach(Destroyable::destroy);
    }

    public void bindUniform(BlockLayer layer) {
        GlMutableBuffer modelUniform = modelUniforms.get(layer);
        modelUniform.bind(GL_UNIFORM_BUFFER);
        modelUniform.bindRange(GL_UNIFORM_BUFFER, 0, 0, UNIFORM_BUFFER_SIZE);
        modelUniform.unbind(GL_UNIFORM_BUFFER);
    }

    public static RenderChunk from(Position pos) {
        return new RenderChunk(pos,
                new EnumMap<>(BlockLayer.class) {
                    {
                        this.put(BlockLayer.SOLID, BatchMesh.of(GL_TRIANGLES, ChunkFormat.CHUNK_FORMAT, RENDER_CHUNK_SIZE, 4096));
                        this.put(BlockLayer.OPAQUE, BatchMesh.of(GL_TRIANGLES, ChunkFormat.CHUNK_FORMAT, RENDER_CHUNK_SIZE, 4096));
                        this.put(BlockLayer.TRANSPARENT, BatchMesh.of(GL_TRIANGLES, ChunkFormat.CHUNK_FORMAT, RENDER_CHUNK_SIZE, 512));
                    }
                },
                new EnumMap<>(BlockLayer.class) {
                    {
                        this.put(BlockLayer.SOLID, createUniformModelBuffer());
                        this.put(BlockLayer.OPAQUE, createUniformModelBuffer());
                        this.put(BlockLayer.TRANSPARENT, createUniformModelBuffer());
                    }
                }
        );
    }

    private static GlMutableBuffer createUniformModelBuffer() {
        GlMutableBuffer modelUniform = new GlMutableBuffer(GL_DYNAMIC_DRAW);
        modelUniform.bind(GL_UNIFORM_BUFFER);
        modelUniform.allocate(GL_UNIFORM_BUFFER, UNIFORM_BUFFER_SIZE);
        modelUniform.unbind(GL_UNIFORM_BUFFER);
        return modelUniform;
    }

    public void setChunk(ChunkPosition position, EnumMap<BlockLayer, VerticesData> newData) {
        int x = position.x();
        int y = position.y();
        int z = position.z();
        int idx = idx(x, y, z);

        final RCUniformSynchronizer synchronizer = new RCUniformSynchronizer(position, this);
        if (newData == null) {
            for (BlockLayer layer : BlockLayer.values()) {
                synchronizer.setLayer(layer);
                passes.get(layer).update(idx, null, synchronizer);
            }
        } else {
            for (BlockLayer layer : BlockLayer.values()) {
                synchronizer.setLayer(layer);
                passes.get(layer).update(idx, newData.get(layer), synchronizer);
            }
        }
    }

    public void setChunkPosition(BlockLayer layer, int drawId, ChunkPosition cp) {
        setChunkPosition(
                layer,
                drawId,
                cp.x() * IChunk.CHUNK_WIDTH,
                cp.y() * IChunk.CHUNK_HEIGHT,
                cp.z() * IChunk.CHUNK_LENGTH
        );
    }

    public void setChunkPosition(BlockLayer layer, int drawId, int x, int y, int z) {
        ByteBuffer matrixBuffer = null;

        try {
            matrixBuffer = MemoryUtil.memCalloc(UNIFORM_MATRIX_SIZE)
                    .putFloat(0, 1.0f)
                    .putFloat(5 * Float.BYTES, 1.0f)
                    .putFloat(10 * Float.BYTES, 1.0f)
                    .putFloat(12 * Float.BYTES, x)
                    .putFloat(13 * Float.BYTES, y)
                    .putFloat(14 * Float.BYTES, z)
                    .putFloat(15 * Float.BYTES, 1.0f)
                    .position(0)
                    .limit(UNIFORM_MATRIX_SIZE);

            final GlMutableBuffer modelUniform = modelUniforms.get(layer);
            modelUniform.bind(GL_UNIFORM_BUFFER);
            modelUniform.upload(GL_UNIFORM_BUFFER, UNIFORM_MATRIX_SIZE * drawId, matrixBuffer);
            modelUniform.unbind(GL_UNIFORM_BUFFER);
        } finally {
            if (matrixBuffer != null) {
                MemoryUtil.memFree(matrixBuffer);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderChunk that = (RenderChunk) o;
        return pos.equals(that.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }

    static int idx(int x, int y, int z) {
        return make(x, 0) | make(y, 1) | make(z, 2);
    }

    static int make(float c, int order) {
        int coord = (int) abs(c % CHUNKS_COUNT);
        return coord << CHUNK_COORDINATE_BITS * order;
    }

    public record Position(int x, int y, int z) {
        public static Position from(ChunkPosition p) {
            return new Position(make(p.x()), make(p.y()), make(p.z()));
        }

        public static Position from(IChunk c) {
            return new Position(make(c.getX()), make(c.getY()), make(c.getZ()));
        }

        static int make(float c) {
            if (c < 0) {
                return (int) Math.floor(c / CHUNKS_COUNT);
            } else {
                return (int) Math.ceil((c + 1) / CHUNKS_COUNT) - 1;
            }
        }
    }
}
