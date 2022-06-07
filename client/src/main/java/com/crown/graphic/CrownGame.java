package com.crown.graphic;

import com.crown.graphic.camera.Camera;
import com.crown.graphic.util.Destroyable;
import com.crown.output.window.Window;

public abstract class CrownGame implements Destroyable {
    protected final Window window;
    protected final Camera camera;

    public CrownGame(int width, int height, String title) {
        GraphicsLibrary.init();

        this.window = new Window(width, height, title);
        this.camera = new Camera();

        this.onResize(window, width, height);
        this.window.setResizeCallback(this::onResize);
    }

    protected void onResize(Window window, int width, int height) {
        camera.onResize(window, width, height);
    }

    @Override
    public void destroy() {
        window.destroy();
        GraphicsLibrary.destroy();
    }
}
