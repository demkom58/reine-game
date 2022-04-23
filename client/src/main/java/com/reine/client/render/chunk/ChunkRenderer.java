package com.reine.client.render.chunk;

import com.crown.graphic.shader.ShaderProgram;
import com.crown.graphic.texture.TextureManager;
import com.crown.graphic.unit.Mesh;
import com.crown.graphic.unit.Model;
import com.reine.block.Block;
import com.reine.client.render.Renderer;
import com.reine.client.render.block.BlockModelManager;
import com.reine.util.Axis;
import com.reine.util.CrownMath;
import com.reine.util.WorldSide;
import com.reine.world.chunk.ChunkPosition;
import com.reine.world.chunk.IChunk;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class ChunkRenderer {
    private final Renderer renderer;
    private final TextureManager textureManager;

    private final Map<ChunkPosition, FaceChunk> facedChunks = new HashMap<>();
    private final Map<ChunkPosition, EnumMap<RenderPass, Model>> renderChunks = new HashMap<>();

    private final ChunkMesher mesher = new ChunkMesher(this::constructMesh);

    public ChunkRenderer(Renderer renderer, TextureManager textureManager) {
        this.renderer = renderer;
        this.textureManager = textureManager;
    }

    public void setChunk(@NotNull IChunk chunk) {
        setChunk(ChunkPosition.fromChunk(chunk), chunk);
    }

    public void setChunk(ChunkPosition position, IChunk chunk) {
        FaceChunk newFacedChunk = chunk != null
                ? FaceChunk.build(chunk)
                : null;

        EnumMap<RenderPass, Model> newModelsChunk = newFacedChunk != null
                ? this.mesher.mesh(chunk, newFacedChunk)
                : null;

        Collection<Model> models = newModelsChunk.values();
        System.out.println(position + " built, models: " + models.size());
        for (Model model : models) {
            System.out.println("Model meshes: " + model.getMeshes().size());
        }

        FaceChunk oldFacedChunk = this.facedChunks.put(position, newFacedChunk);
        EnumMap<RenderPass, Model> oldModelsChunk = this.renderChunks.put(position, newModelsChunk);

        if (oldFacedChunk != null) {
            oldFacedChunk.destroy();
        }

        if (oldModelsChunk != null) {
            oldModelsChunk.values().forEach(Model::destroy);
        }
    }

    public void updateBlock(ChunkPosition position, IChunk chunk, int x, int y, int z) {
        FaceChunk faceChunk = facedChunks.get(position);

        faceChunk.update(chunk, x, y, z);

        EnumMap<RenderPass, Model> newMesh = mesher.mesh(chunk, faceChunk);
        EnumMap<RenderPass, Model> oldMesh = renderChunks.put(position, newMesh);
        if (oldMesh != null) {
            oldMesh.values().forEach(Model::destroy);
        }
    }

    public void render(ShaderProgram program, IChunk chunk) {
        EnumMap<RenderPass, Model> renderModel = renderChunks.get(ChunkPosition.fromChunk(chunk));

        renderer.oneMainMatrix
                .translate(chunk.getX(), chunk.getY(), chunk.getZ(), renderer.modelMatrix)
                .get(renderer.modelBuffer);
        program.setUniformMatrix4fv("model", false, renderer.modelBuffer);

        glEnable(GL_DEPTH_TEST);
        textureManager.getAtlas().use(0);
//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        for (Model model : renderModel.values()) {
            final List<Mesh> meshes = model.getMeshes();
            if (meshes.isEmpty()) {
                continue;
            }

            for (Mesh mesh : meshes) {
                mesh.bind();
                mesh.draw();
            }
        }

        glBindVertexArray(0);
//        for (int x = 0; x < IChunk.CHUNK_WIDTH; x++) {
//            for (int y = 0; y < IChunk.CHUNK_HEIGHT; y++) {
//                for (int z = 0; z < IChunk.CHUNK_LENGTH; z++) {
//
//                    int blockId = chunk.getBlockId(x, y, z);
//                    if (blockId == 0) {
//                        continue;
//                    }
//
//                    renderer.oneMainMatrix
//                            .translate(
//                                    chunk.getX() * IChunk.CHUNK_WIDTH + x,
//                                    chunk.getY() * IChunk.CHUNK_HEIGHT + y,
//                                    chunk.getZ() * IChunk.CHUNK_LENGTH + z,
//                                    renderer.modelMatrix
//                            ).get(renderer.modelBuffer);
//
//                    program.setUniformMatrix4fv("mesh", false, renderer.modelBuffer);
//
//                    Mesh mesh = blockModelManager.getModel(blockId);
//                    mesh.bind();
//                    mesh.draw();
//                }
//            }
//        }
    }

    public Mesh constructMesh(Vector3f str, Vector3f end, WorldSide side, int blockId) {
        CrownMath.minMaxSwap(str, end);

        System.out.println("Building mesh " + str + " - " + end + ": " + side);

        final FloatBuffer uvB = MemoryUtil.memAllocFloat(3 * 2 * 2);
        final FloatBuffer posB = MemoryUtil.memAllocFloat(3 * 3 * 2);

        switch (side.axis()) {
            case X -> posB.put(new float[]{
                    str.x, str.y, str.z,
                    str.x, str.y, end.z,
                    str.x, end.y, str.z,

                    str.x, str.y, end.z,
                    str.x, end.y, str.z,
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

        final float[] uv = new float[]{
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,

                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
        };
        textureManager.atlasify(Block.byId(blockId).getTexture(), uv);
        uvB.put(uv);

        return Mesh.triangles()
                .positions(0, posB.flip(), 3, false)
                .attribute(1, uvB.flip(), 2, false)
                .build();
    }

}
