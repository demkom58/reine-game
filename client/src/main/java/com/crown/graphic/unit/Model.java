package com.crown.graphic.unit;

import com.crown.graphic.util.Destroyable;

import java.util.List;

public class Model implements Destroyable {
    private final List<Mesh> meshes;

    public Model(List<Mesh> meshes) {
        this.meshes = meshes;
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    @Override
    public void destroy() {
        for (Mesh mesh : meshes) {
            mesh.destroy();
        }
    }
}
