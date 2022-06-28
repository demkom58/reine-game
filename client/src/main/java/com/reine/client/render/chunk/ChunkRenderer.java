package com.reine.client.render.chunk;

import com.crown.graphic.camera.Camera;
import com.crown.graphic.gl.shader.GlShaderProgram;
import com.crown.graphic.unit.BatchMesh;
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
    public static final int MODEL_UBO = 0;
    public static final int VIEW_MATRIX_UNIFORM = 0;
    public static final int PROJECTION_MATRIX_UNIFORM = 1;
    public static final int ALPHA_THRESHOLD_UNIFORM = 2;

    private final Renderer renderer;
    private final TextureManager textureManager;
    private final ChunkMesher mesher;

    private final Map<ChunkPosition, FaceChunk> facedChunks = new HashMap<>();

    private final Map<RenderChunk.Position, RenderChunk> renderChunks = new HashMap<>();


    public ChunkRenderer(Renderer renderer, TextureManager textureManager) {
        this.renderer = renderer;
        this.textureManager = textureManager;
        this.mesher = new ChunkMesher(textureManager);
    }

    public void setChunk(ChunkGrid grid, @NotNull IChunk chunk) {
        setChunk(grid, ChunkPosition.fromChunk(chunk), chunk);
    }

    public void setChunk(ChunkGrid grid, ChunkPosition position, IChunk chunk) {
        RenderChunk.Position batchPos = RenderChunk.Position.from(position);
        if (chunk == null) {
            FaceChunk remove = this.facedChunks.remove(position);
            if (remove != null) {
                remove.destroy();
            }

            RenderChunk renderChunk = renderChunks.get(batchPos);
            if (renderChunk != null) {
                renderChunk.setChunk(position, null);
            }

            return;
        }

        FaceChunk newFacedChunk = FaceChunk.build(grid, chunk);
        RenderChunk renderChunk = renderChunks.computeIfAbsent(batchPos, RenderChunk::from);

        try (var result = this.mesher.mesh(chunk, newFacedChunk)) {
            renderChunk.setChunk(position, result.vertices());
        }

        FaceChunk oldFacedChunk = this.facedChunks.put(position, newFacedChunk);
        if (oldFacedChunk != null) {
            oldFacedChunk.destroy();
        }
    }

//    public void updateBlock(ChunkGrid grid, ChunkPosition position, IChunk chunk, int x, int y, int z) {
//        FaceChunk faceChunk = facedChunks.get(position);
//        faceChunk.update(grid, chunk, x, y, z);
//
//        final RenderChunk newChunk = new RenderChunk(x, y, z, mesher.mesh(chunk, faceChunk));
//        final RenderChunk oldChunk = renderChunks.put(position, newChunk);
//
//        if (oldChunk != null) {
//            oldChunk.destroy();
//        }
//    }

    public Set<RenderChunk> getRenderChunks(Collection<IChunk> chunks) {
        final Set<RenderChunk> toRender = new HashSet<>(chunks.size());
        for (IChunk chunk : chunks) {
            toRender.add(renderChunks.get(RenderChunk.Position.from(chunk)));
        }
        return toRender;
    }

    public void render(Camera camera, GlShaderProgram program, Collection<IChunk> chunks) {
        final Collection<RenderChunk> uniqueChunks = new HashSet<>(chunks.size());
        for (IChunk chunk : chunks) {
            int x = chunk.getX() * CHUNK_WIDTH;
            int y = chunk.getY() * CHUNK_HEIGHT;
            int z = chunk.getZ() * CHUNK_LENGTH;

            if (camera.isBoxInFrustum(x, y, z, CHUNK_WIDTH, CHUNK_HEIGHT, CHUNK_LENGTH) && !chunk.isEmpty()) {
                uniqueChunks.add(renderChunks.get(RenderChunk.Position.from(chunk)));
            }
        }

        List<RenderChunk> toRender = new ArrayList<>(uniqueChunks);
        Vector3f currentChunk = new Vector3f(camera.getPosition()).div(RenderChunk.CHUNKS_GROUP_AXIS_SIZE);
        toRender.sort(Comparator.comparingInt(k -> {
            RenderChunk.Position p = k.pos();
            return Math.round(currentChunk.distanceSquared(p.x(), p.y(), p.z()));
        }));

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
        BatchMesh mesh = chunk.passes().get(layer);
        if (mesh == null) {
            return;
        }

        chunk.bindUniform(layer);
        mesh.bind();
        mesh.draw();
    }
}
