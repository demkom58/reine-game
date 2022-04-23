package com.reine.client.render.block;

import com.crown.graphic.texture.TextureManager;
import com.crown.graphic.unit.Mesh;
import com.crown.mesh.CubeFactory;
import com.reine.block.Block;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.Collection;

import static org.lwjgl.system.MemoryStack.stackPush;

public class BlockModelManager {
    private Mesh[] meshes;

    public void reload(TextureManager textureManager) {
        Collection<Block> blocks = Block.values();

        if (this.meshes != null) {
            for (Mesh mesh : meshes) {
                mesh.close();
            }
        }

        this.meshes = new Mesh[blocks.size()];

        try (MemoryStack stack = stackPush()) {
            float[] position = CubeFactory.vertices();
            FloatBuffer positionBuffer = stack.callocFloat(position.length)
                    .put(position)
                    .flip();

            FloatBuffer blockUVBuffer = null;

            for (Block block : blocks) {
                if (block.getTexture() == null) {
                    continue;
                }

                float[] blockUV = CubeFactory.texAllSides();
                textureManager.atlasify(block.getTexture(), blockUV);

                if (blockUVBuffer == null) {
                    blockUVBuffer = stack.callocFloat(blockUV.length).put(blockUV).flip();
                } else {
                    blockUVBuffer.rewind().put(blockUV).flip();
                }

                meshes[block.getId()] = Mesh.triangles()
                        .positions(0, positionBuffer, 3, false)
                        .attribute(1, blockUVBuffer, 2, false)
                        .build();
            }
        }
    }

    public Mesh getModel(int blockId) {
        return meshes[blockId];
    }
}
