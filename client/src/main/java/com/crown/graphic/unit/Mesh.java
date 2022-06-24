package com.crown.graphic.unit;

import com.crown.graphic.util.Destroyable;

public interface Mesh extends Destroyable {
    void bind();

    void unbind();

    void draw();
}
