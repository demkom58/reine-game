package com.crown;

import com.crown.graphic.camera.Camera;
import com.crown.graphic.gl.GraphicsLibrary;
import com.crown.graphic.util.Destroyable;
import com.crown.output.window.Window;
import com.crown.util.GameLoop;

import java.awt.*;

public abstract class CrownGame implements Game, Destroyable {
    protected final Window window;
    protected final Camera camera;
    protected final GameLoop gameLoop;

    public CrownGame(int width, int height, String title, GameLoop gameLoop) {
        GraphicsLibrary.init();

        this.window = new Window(width, height, title);
        this.camera = new Camera();
        this.gameLoop = gameLoop;
    }

    public void start() {
        final Dimension size = window.getSize();
        this.onResize(window, size.width, size.height);
        this.window.setResizeCallback(this::onResize);

        gameLoop.init();
        gameLoop.loop(this);
    }

    protected void onResize(Window window, int width, int height) {
        camera.onResize(window, width, height);
    }

    @Override
    public void destroy() {
        window.destroy();
        GraphicsLibrary.destroy();
    }

    @Override
    public Window getWindow() {
        return window;
    }
}
