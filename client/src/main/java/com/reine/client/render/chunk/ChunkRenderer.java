package com.reine.client.render.chunk;

import com.crown.graphic.camera.Camera;
import com.crown.graphic.gl.shader.GlShaderProgram;
import com.crown.graphic.unit.ComposedMesh;
import com.reine.client.TextureManager;
import com.reine.client.render.Renderer;
import com.reine.client.render.chunk.mesh.ChunkMesher;
import com.reine.client.render.chunk.mesh.RenderChunk;
import com.reine.client.render.chunk.util.FaceChunk;
import com.reine.block.BlockLayer;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.ChunkPosition;
import com.reine.world.chunk.IChunk;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.*;

import static com.reine.world.chunk.IChunk.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer {
    public static final int MODEL_MATRIX_UNIFORM = 0;
    public static final int VIEW_MATRIX_UNIFORM = 1;
    public static final int PROJECTION_MATRIX_UNIFORM = 2;
    public static final int ALPHA_THRESHOLD_UNIFORM = 3;

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

        Vector3f currentChunk = new Vector3f(camera.getPosition()).div(CHUNK_WIDTH);
        toRender.sort(Comparator.comparingInt(k -> Math.round(currentChunk.distanceSquared(k.x(), k.y(), k.z()))));

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        for (int i = toRender.size() - 1; i >= 0; i--) {
            final RenderChunk chunk = toRender.get(i);
            renderLayer(program, chunk, BlockLayer.SOLID);
        }

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        program.setUniform1f(ALPHA_THRESHOLD_UNIFORM, 0.5f);
        for (int i = toRender.size() - 1; i >= 0; i--) {
            renderLayer(program, toRender.get(i), BlockLayer.OPAQUE);
        }
        program.setUniform1f(ALPHA_THRESHOLD_UNIFORM, 0);

        glDepthMask(false);
        for (int i = toRender.size() - 1; i >= 0; i--) {
            renderLayer(program, toRender.get(i), BlockLayer.TRANSPARENT);
        }
        glDepthMask(true);
        glDisable(GL_BLEND);

        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        glBindVertexArray(0);
    }

    private void renderLayer(GlShaderProgram program, RenderChunk chunk, BlockLayer layer) {
        ComposedMesh solid = chunk.passes().get(layer);
        if (solid == null) {
            return;
        }

        setChunkPosition(program, chunk);

        solid.bind();
        solid.draw();
    }

    private void setChunkPosition(GlShaderProgram program, RenderChunk chunk) {
        renderer.oneMainMatrix
                .translate(
                        chunk.x() * CHUNK_WIDTH,
                        chunk.y() * CHUNK_HEIGHT,
                        chunk.z() * CHUNK_LENGTH,
                        renderer.modelMatrix
                ).get(renderer.modelBuffer);
        program.setUniformMatrix4fv(MODEL_MATRIX_UNIFORM, false, renderer.modelBuffer);
    }
}
