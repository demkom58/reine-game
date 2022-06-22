package com.reine.client.render.chunk;

import com.crown.graphic.camera.Camera;
import com.crown.graphic.gl.buffer.VerticesData;
import com.crown.graphic.gl.shader.GlShaderProgram;
import com.reine.client.TextureManager;
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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static com.reine.world.chunk.IChunk.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
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
        Mesh solid = chunk.passes().get(RenderPass.SOLID);
        if (solid == null) {
            return;
        }

        setChunkPosition(program, chunk);

        solid.bind();
        solid.draw();
    }

    private void renderTransparent(GlShaderProgram program, RenderChunk chunk) {
        Mesh transparent = chunk.passes().get(RenderPass.TRANSPARENT);
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

    public Mesh compileMesh(List<ChunkQuad> quads) {
        final int quadsCount = quads.size();
        if (quadsCount == 0) {
            return null;
        }

        final int verticesCount = quadsCount * QUAD_TRIANGLES * TRIANGLE_VERTICES;
        final int bytesCount = verticesCount * ChunkFormat.CHUNK_FORMAT.getStride();
        final ByteBuffer vertexData = MemoryUtil.memAlloc(bytesCount);

        ChunkFormat.write(textureManager, quads, vertexData);

        vertexData.flip();
        final Mesh chunk = Mesh.of(GL_TRIANGLES, GL_STATIC_DRAW, new VerticesData(ChunkFormat.CHUNK_FORMAT, vertexData));
        MemoryUtil.memFree(vertexData);

        return chunk;
    }

}
