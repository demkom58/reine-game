package com.reine.client.render.chunk;

import com.crown.graphic.camera.Camera;
import com.crown.graphic.gl.shader.GlShaderProgram;
import com.crown.graphic.unit.ComposedMesh;
import com.reine.client.TextureManager;
import com.reine.client.render.Renderer;
import com.reine.client.render.chunk.mesh.ChunkMesher;
import com.reine.client.render.chunk.mesh.RenderChunk;
import com.reine.client.render.chunk.util.FaceChunk;
import com.reine.client.render.chunk.util.RenderPass;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.ChunkPosition;
import com.reine.world.chunk.IChunk;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.reine.world.chunk.IChunk.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer {
    private final Renderer renderer;
    private final TextureManager textureManager;
    private final ChunkMesher mesher;

    private final Map<ChunkPosition, FaceChunk> facedChunks = new HashMap<>();
    private final Map<ChunkPosition, RenderChunk> renderChunks = new HashMap<>();


    public ChunkRenderer(Renderer renderer, TextureManager textureManager) {
        this.renderer = renderer;
        this.textureManager = textureManager;
        this.mesher = new ChunkMesher(textureManager);
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

    public List<RenderChunk> getRenderChunks(Collection<IChunk> chunks) {
        final List<RenderChunk> toRender = new ArrayList<>(chunks.size());
        for (IChunk chunk : chunks) {
            toRender.add(renderChunks.get(ChunkPosition.fromChunk(chunk)));
        }
        return toRender;
    }

    public void render(Camera camera, GlShaderProgram program, Collection<IChunk> chunks) {
        final List<RenderChunk> toRender = new ArrayList<>(chunks.size());
        for (IChunk chunk : chunks) {
            int x = chunk.getX() * CHUNK_WIDTH;
            int y = chunk.getY() * CHUNK_HEIGHT;
            int z = chunk.getZ() * CHUNK_LENGTH;

            if (camera.isBoxInFrustum(x, y, z, CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_LENGTH)) {
                toRender.add(renderChunks.get(ChunkPosition.fromChunk(chunk)));
            }
        }
//        System.out.println("Chunks to render: " + toRender.size());

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        for (RenderChunk chunk : toRender) {
            renderSolid(program, chunk);
        }

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        for (RenderChunk chunk : toRender) {
            renderTransparent(program, chunk);
        }
        glDisable(GL_BLEND);

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        glBindVertexArray(0);
    }

    private void renderSolid(GlShaderProgram program, RenderChunk chunk) {
        ComposedMesh solid = chunk.passes().get(RenderPass.SOLID);
        if (solid == null) {
            return;
        }

        setChunkPosition(program, chunk);

        solid.bind();
        solid.draw();
    }

    private void renderTransparent(GlShaderProgram program, RenderChunk chunk) {
        ComposedMesh transparent = chunk.passes().get(RenderPass.TRANSPARENT);
        if (transparent == null) {
            return;
        }

        setChunkPosition(program, chunk);

        transparent.bind();
        transparent.draw();
    }

    private void setChunkPosition(GlShaderProgram program, RenderChunk chunk) {
        renderer.oneMainMatrix
                .translate(
                        chunk.x() * CHUNK_WIDTH,
                        chunk.y() * CHUNK_HEIGHT,
                        chunk.z() * CHUNK_LENGTH,
                        renderer.modelMatrix
                ).get(renderer.modelBuffer);
        program.setUniformMatrix4fv(0, false, renderer.modelBuffer);
    }
}
