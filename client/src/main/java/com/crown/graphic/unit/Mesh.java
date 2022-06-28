package com.crown.graphic.unit;

import com.crown.graphic.util.Bindable;
import com.crown.graphic.util.Destroyable;
import com.crown.graphic.util.Unbindable;

public interface Mesh extends Destroyable, Bindable, Unbindable {
    void draw();
}
