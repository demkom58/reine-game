package com.reine.client.render.chunk;

import com.crown.graphic.shader.ShaderProgram;
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

import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer {
    private static final int TRIANGLE_VERTICES = 3;
    private static final int QUAD_TRIANGLES = 2;


    private final Renderer renderer;
    private final TextureManager textureManager;

    private final Map<ChunkPosition, FaceChunk> facedChunks = new HashMap<>();
    private final Map<ChunkPosition, EnumMap<RenderPass, Mesh>> renderChunks = new HashMap<>();

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

        EnumMap<RenderPass, Mesh> newMeshChunk = newFacedChunk != null
                ? this.mesher.mesh(chunk, newFacedChunk)
                : null;

        FaceChunk oldFacedChunk = this.facedChunks.put(position, newFacedChunk);
        EnumMap<RenderPass, Mesh> oldMeshChunk = this.renderChunks.put(position, newMeshChunk);

        if (oldFacedChunk != null) {
            oldFacedChunk.destroy();
        }

        if (oldMeshChunk != null) {
            oldMeshChunk.values().forEach(Mesh::destroy);
        }
    }

    public void updateBlock(ChunkGrid grid, ChunkPosition position, IChunk chunk, int x, int y, int z) {
        FaceChunk faceChunk = facedChunks.get(position);

        faceChunk.update(grid, chunk, x, y, z);

        EnumMap<RenderPass, Mesh> newMesh = mesher.mesh(chunk, faceChunk);
        EnumMap<RenderPass, Mesh> oldMesh = renderChunks.put(position, newMesh);
        if (oldMesh != null) {
            oldMesh.values().forEach(Mesh::destroy);
        }
    }

    public void render(ShaderProgram program, IChunk chunk) {
        EnumMap<RenderPass, Mesh> meshes = renderChunks.get(ChunkPosition.fromChunk(chunk));

        renderer.oneMainMatrix
                .translate(
                        chunk.getX() * IChunk.CHUNK_WIDTH,
                        chunk.getY() * IChunk.CHUNK_HEIGHT,
                        chunk.getZ() * IChunk.CHUNK_LENGTH,
                        renderer.modelMatrix
                ).get(renderer.modelBuffer);
        program.setUniformMatrix4fv("model", false, renderer.modelBuffer);
        textureManager.getAtlas().use(0);

        glEnable(GL_DEPTH_TEST);

        Mesh solid = meshes.get(RenderPass.SOLID);
        solid.bind();
        solid.draw();

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
        Mesh transparent = meshes.get(RenderPass.TRANSPARENT);
        transparent.bind();
        transparent.draw();
//        glDisable(GL_BLEND);
//        glDisable(GL_CULL_FACE);

        glDisable(GL_DEPTH_TEST);
        glBindVertexArray(0);
    }


    public Mesh compileMesh(List<ChunkQuad> quads) {
        final int quadsCount = quads.size();
        final FloatBuffer texB = MemoryUtil.memCallocFloat(quadsCount * 4); // vec4(atlas x, atlas y, tile width, tile height)
        final FloatBuffer faceB = MemoryUtil.memCallocFloat(TRIANGLE_VERTICES * QUAD_TRIANGLES * quadsCount * 3); // vec3(x, y, z)
        final FloatBuffer posB = MemoryUtil.memCallocFloat(TRIANGLE_VERTICES * QUAD_TRIANGLES * quadsCount * 3); // vec3(x, y, z)

        for (ChunkQuad quad : quads) {
            final Vector3f str = quad.start();
            final Vector3f end = quad.end();

            final Block block = Block.byId(quad.blockId());
            final WorldSide side = quad.side();
            final Axis axis = side.axis();

            switch (axis) {
                case X -> posB.put(new float[]{
                        str.x, str.y, str.z,
                        str.x, str.y, end.z,
                        str.x, end.y, str.z,

                        str.x, end.y, str.z,
                        str.x, str.y, end.z,
                        str.x, end.y, end.z,
                });
                case Y -> posB.put(new float[]{
                        str.x, str.y, str.z,
                        end.x, str.y, str.z,
                        str.x, str.y, end.z,

                        end.x, str.y, str.z,
                        str.x, str.y, end.z,
                        end.x, str.y, end.z,
                });
                case Z -> posB.put(new float[]{
                        str.x, str.y, str.z,
                        end.x, str.y, str.z,
                        str.x, end.y, str.z,

                        end.x, str.y, str.z,
                        str.x, end.y, str.z,
                        end.x, end.y, str.z,
                });
            }

            Vector3f normal = axis.getVector();
            faceB.put(new float[] {
                    normal.x, normal.y, normal.z,
                    normal.x, normal.y, normal.z,
                    normal.x, normal.y, normal.z,
                    normal.x, normal.y, normal.z,
                    normal.x, normal.y, normal.z,
                    normal.x, normal.y, normal.z,
            });
            texB.put(textureManager.normalizedDimension(block.getTexture(side)));
        }

        return Mesh.triangles()
                .positions(0, posB.flip(), 3, false)
                .attribute(1, faceB.flip(), 3, false)
                .attribute(2, texB.flip(), 4, false)
                .attributeDivisor(2, 6)
                .build();
    }

}
