package com.reine.client.render.chunk;

import com.crown.graphic.shader.ShaderProgram;
import com.crown.graphic.texture.BindlessTexture2D;
import com.crown.graphic.texture.TextureManager;
import com.crown.graphic.unit.Mesh;
import com.reine.block.Block;
import com.reine.client.render.Renderer;
import com.reine.util.Axis;
import com.reine.util.WorldSide;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.ChunkPosition;
import com.reine.world.chunk.IChunk;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import static com.reine.world.chunk.IChunk.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer {
    private static final int TRIANGLE_VERTICES = 3;
    private static final int QUAD_TRIANGLES = 2;


    private final Renderer renderer;
    private final TextureManager textureManager;

    private final Map<ChunkPosition, FaceChunk> facedChunks = new HashMap<>();
    private final Map<ChunkPosition, RenderChunk> renderChunks = new HashMap<>();

    private final ChunkMesher mesher = new ChunkMesher(this::compileMesh);

    public ChunkRenderer(Renderer renderer, TextureManager textureManager) {
        this.renderer = renderer;
        this.textureManager = textureManager;
    }

    public void setChunk(ChunkGrid grid, @NotNull IChunk chunk) {
        setChunk(grid, ChunkPosition.fromChunk(chunk), chunk);
    }

    public void setChunk(ChunkGrid grid, ChunkPosition position, IChunk chunk) {
        FaceChunk newFacedChunk = chunk != null
                ? FaceChunk.build(grid, chunk)
                : null;

        RenderChunk renderChunk = null;
        if (newFacedChunk != null) {
            renderChunk = new RenderChunk(chunk.getX(), chunk.getY(), chunk.getZ(), this.mesher.mesh(chunk, newFacedChunk));
        }

        FaceChunk oldFacedChunk = this.facedChunks.put(position, newFacedChunk);
        RenderChunk oldRenderChunk = this.renderChunks.put(position, renderChunk);

        if (oldFacedChunk != null) {
            oldFacedChunk.destroy();
        }

        if (oldRenderChunk != null) {
            oldRenderChunk.destroy();
        }
    }

    public void updateBlock(ChunkGrid grid, ChunkPosition position, IChunk chunk, int x, int y, int z) {
        FaceChunk faceChunk = facedChunks.get(position);
        faceChunk.update(grid, chunk, x, y, z);

        final RenderChunk newChunk = new RenderChunk(x, y, z, mesher.mesh(chunk, faceChunk));
        final RenderChunk oldChunk = renderChunks.put(position, newChunk);

        if (oldChunk != null) {
            oldChunk.destroy();
        }
    }

    public void render(ShaderProgram program, Collection<IChunk> chunks) {
        final List<RenderChunk> toRender = new ArrayList<>(chunks.size());
        for (IChunk chunk : chunks) {
            toRender.add(renderChunks.get(ChunkPosition.fromChunk(chunk)));
        }

        glEnable(GL_DEPTH_TEST);

        for (RenderChunk chunk : toRender) {
            renderSolid(program, chunk);
        }

        for (RenderChunk chunk : toRender) {
            renderTransparent(program, chunk);
        }

        glDisable(GL_DEPTH_TEST);
        glBindVertexArray(0);
    }

    private void renderSolid(ShaderProgram program, RenderChunk chunk) {
        setChunkPosition(program, chunk);

        Mesh solid = chunk.passes().get(RenderPass.SOLID);
        solid.bind();
        solid.draw();
    }

    private void renderTransparent(ShaderProgram program, RenderChunk chunk) {
        setChunkPosition(program, chunk);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        Mesh transparent = chunk.passes().get(RenderPass.TRANSPARENT);
        transparent.bind();
        transparent.draw();

        glDisable(GL_BLEND);
    }

    private void setChunkPosition(ShaderProgram program, RenderChunk chunk) {
        renderer.oneMainMatrix
                .translate(
                        chunk.x() * CHUNK_WIDTH,
                        chunk.y() * CHUNK_HEIGHT,
                        chunk.z() * CHUNK_LENGTH,
                        renderer.modelMatrix
                ).get(renderer.modelBuffer);
        program.setUniformMatrix4fv("model", false, renderer.modelBuffer);
    }

    public Mesh compileMesh(List<ChunkQuad> quads) {
        final int quadsCount = quads.size();

        final FloatBuffer texB = MemoryUtil.memCallocFloat(TRIANGLE_VERTICES * QUAD_TRIANGLES * quadsCount * 3); // vec3(handle, x, y)
        final FloatBuffer posB = MemoryUtil.memCallocFloat(TRIANGLE_VERTICES * QUAD_TRIANGLES * quadsCount * 3); // vec3(x, y, z)

        for (ChunkQuad quad : quads) {
            final Vector3f str = quad.start();
            final Vector3f end = quad.end();

            final Block block = Block.byId(quad.blockId());
            final WorldSide side = quad.side();
            final Axis axis = side.axis();

            long texId = textureManager.getId(block.getTexture(side));

            switch (axis) {
                case X -> {
                    posB.put(new float[]{
                            str.x, str.y, str.z,
                            str.x, str.y, end.z,
                            str.x, end.y, str.z,

                            str.x, end.y, str.z,
                            str.x, str.y, end.z,
                            str.x, end.y, end.z,
                    });
                    texB.put(new float[]{
                            texId, str.y, str.z,
                            texId, str.y, end.z,
                            texId, end.y, str.z,

                            texId, end.y, str.z,
                            texId, str.y, end.z,
                            texId, end.y, end.z,
                    });
                }
                case Y -> {
                    posB.put(new float[]{
                            str.x, str.y, str.z,
                            end.x, str.y, str.z,
                            str.x, str.y, end.z,

                            end.x, str.y, str.z,
                            str.x, str.y, end.z,
                            end.x, str.y, end.z,
                    });
                    texB.put(new float[]{
                            texId, str.x, str.z,
                            texId, end.x, str.z,
                            texId, str.x, end.z,

                            texId, end.x, str.z,
                            texId, str.x, end.z,
                            texId, end.x, end.z,
                    });
                }
                case Z -> {
                    posB.put(new float[]{
                            str.x, str.y, str.z,
                            end.x, str.y, str.z,
                            str.x, end.y, str.z,

                            end.x, str.y, str.z,
                            str.x, end.y, str.z,
                            end.x, end.y, str.z,
                    });
                    texB.put(new float[]{
                            texId, str.x, str.y,
                            texId, end.x, str.y,
                            texId, str.x, end.y,

                            texId, end.x, str.y,
                            texId, str.x, end.y,
                            texId, end.x, end.y,
                    });
                }
            }

        }

        return Mesh.triangles()
                .positions(0, posB.flip(), 3, false)
                .attribute(1, texB.flip(), 3, false)
                .build();
    }

}
