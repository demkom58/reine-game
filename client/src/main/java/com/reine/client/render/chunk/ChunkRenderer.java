package com.reine.client.render.chunk;

import com.crown.graphic.camera.Camera;
import com.crown.graphic.shader.ShaderProgram;
import com.crown.graphic.texture.TextureManager;
import com.crown.graphic.unit.Mesh;
import com.reine.block.Block;
import com.reine.client.render.Renderer;
import com.reine.util.WorldSide;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.ChunkPosition;
import com.reine.world.chunk.IChunk;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.reine.world.chunk.IChunk.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer {
    public static final int TRIANGLE_VERTICES = 3;
    public static final int QUAD_TRIANGLES = 2;


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

    public void render(Camera camera, ShaderProgram program, Collection<IChunk> chunks) {
        final List<RenderChunk> toRender = new ArrayList<>(chunks.size());
        for (IChunk chunk : chunks) {
            int x = chunk.getX() * 16;
            int y = chunk.getY() * 16;
            int z = chunk.getZ() * 16;
            if (camera.isBoxInFrustum(x, y, z, 16, 16, 16)) {
                toRender.add(renderChunks.get(ChunkPosition.fromChunk(chunk)));
            }
        }
//        System.out.println("Chunks to render: " + toRender.size());

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        for (RenderChunk chunk : toRender) {
            renderSolid(program, chunk);
        }

        for (RenderChunk chunk : toRender) {
            renderTransparent(program, chunk);
        }

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        glBindVertexArray(0);
    }

    private void renderSolid(ShaderProgram program, RenderChunk chunk) {
        Mesh solid = chunk.passes().get(RenderPass.SOLID);
        if (solid == null) {
            return;
        }

        setChunkPosition(program, chunk);

        solid.bind();
        solid.draw();
    }

    private void renderTransparent(ShaderProgram program, RenderChunk chunk) {
        Mesh transparent = chunk.passes().get(RenderPass.TRANSPARENT);
        if (transparent == null) {
            return;
        }

        setChunkPosition(program, chunk);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

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

        final IntBuffer faceB = MemoryUtil.memCallocInt(TRIANGLE_VERTICES * QUAD_TRIANGLES * quadsCount * 4); // vec3(normal x, normal y, normal z, texId)
        final FloatBuffer posB = MemoryUtil.memCallocFloat(TRIANGLE_VERTICES * QUAD_TRIANGLES * quadsCount * 3); // vec3(x, y, z)

        for (ChunkQuad quad : quads) {
            final Vector3f str = quad.start();
            final Vector3f end = quad.end();

            final WorldSide side = quad.side();
            fillSideVertices(posB, side, str, end);

            int texId = textureManager.getId(Block.byId(quad.blockId()).getTexture(side));
            Vector3f normal = side.vec();

            for (int i = 0; i < 6; i++) {
                faceB.put(new int[] {(int) normal.x, (int) normal.y, (int) normal.z, texId});
            }
        }

        return Mesh.triangles()
                .positions(0, posB.flip(), 3, false)
                .attribute(1, faceB.flip(), 4)
                .build();
    }

    private void fillSideVertices(FloatBuffer posB, WorldSide side, Vector3f str, Vector3f end) {
        switch (side) {
            case WEST -> posB.put(new float[]{
                    str.x, str.y, str.z,
                    str.x, str.y, end.z,
                    str.x, end.y, str.z,

                    str.x, end.y, str.z,
                    str.x, str.y, end.z,
                    str.x, end.y, end.z,
            });
            case EAST -> posB.put(new float[]{
                    str.x, str.y, str.z,
                    str.x, end.y, str.z,
                    str.x, str.y, end.z,

                    str.x, str.y, end.z,
                    str.x, end.y, str.z,
                    str.x, end.y, end.z,
            });
            case DOWN -> posB.put(new float[]{
                    str.x, str.y, str.z,
                    end.x, str.y, str.z,
                    str.x, str.y, end.z,

                    str.x, str.y, end.z,
                    end.x, str.y, str.z,
                    end.x, str.y, end.z,
            });
            case UP -> posB.put(new float[]{
                    str.x, str.y, str.z,
                    str.x, str.y, end.z,
                    end.x, str.y, str.z,

                    end.x, str.y, str.z,
                    str.x, str.y, end.z,
                    end.x, str.y, end.z,
            });
            case NORTH -> posB.put(new float[]{
                    end.x, str.y, str.z,
                    str.x, str.y, str.z,
                    str.x, end.y, str.z,

                    end.x, str.y, str.z,
                    str.x, end.y, str.z,
                    end.x, end.y, str.z,
            });
            case SOUTH -> posB.put(new float[]{
                    str.x, str.y, str.z,
                    end.x, str.y, str.z,
                    str.x, end.y, str.z,

                    str.x, end.y, str.z,
                    end.x, str.y, str.z,
                    end.x, end.y, str.z,
            });
        }
    }

}
