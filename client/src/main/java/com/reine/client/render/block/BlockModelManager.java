package com.reine.client.render.block;

import com.crown.graphic.texture.TextureManager;
import com.crown.graphic.unit.Mesh;
import com.crown.model.CubeFactory;
import com.reine.block.Block;

import java.util.Collection;

public class BlockModelManager {
    private Mesh[] meshes;

    public void reload(TextureManager textureManager) {
        Collection<Block> blocks = Block.values();
        this.meshes = new Mesh[blocks.size()];

        float[] vertices = CubeFactory.vertices();
        for (Block block : blocks) {
            float[] tex = CubeFactory.texAllSides();
            textureManager.atlasify(block.getTexture(), tex);

            meshes[block.getId() - 1] = new Mesh(vertices, tex);
        }
    }

    public Mesh getMesh(int blockId) {
        return meshes[blockId - 1];
    }
}
