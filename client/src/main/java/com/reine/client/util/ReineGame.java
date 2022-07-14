package com.reine.client.util;

import com.crown.CrownGame;
import com.crown.graphic.gl.GraphicsLibrary;
import com.crown.input.keyboard.Keyboard;
import com.crown.input.mouse.Mouse;
import com.crown.output.window.Window;
import com.crown.util.AutoDeltaGameLoop;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class ReineGame extends CrownGame {
    protected final Mouse mouse;
    protected final Keyboard keyboard;

    public ReineGame(int width, int height, String title, int fps, int ups) {
        super(width, height, title, new AutoDeltaGameLoop(fps, ups));
        camera.setAspect(width, height);

        GraphicsLibrary.enableMultiSampling();
        GraphicsLibrary.reversZDepth();

        this.mouse = new Mouse(window);
        this.keyboard = new Keyboard(window);

        mouse.setPositionCallback(this::onCursorMove);
        window.setFocusCallback(this::onFocus);

        window.setSampling(GLFW_SAMPLES, 4);
    }

    @Override
    public void input() {
        if (keyboard.isKeyDown(GLFW_KEY_ESCAPE)) {
            window.setShouldClose(true);
        }

        if (keyboard.isKeyDown(GLFW_KEY_LEFT_ALT)) {
            mouse.ungrabMouseCursor();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void render(float delta) {

    }

    public void onCursorMove(@NotNull Window window, double x, double y) {
        camera.rotate((float) mouse.getDeltaX() / 300f, (float) mouse.getDeltaY() / -300f, 0f);
    }

    public void onFocus(@NotNull Window window, boolean focused) {
        if (focused) {
            mouse.grabMouseCursor();
        } else {
            mouse.setCursorInCenter();
            mouse.ungrabMouseCursor();
        }
    }

    protected void requireExtensions(String... extensions) {
        Set<String> notSupported = GraphicsLibrary.isNotSupportedExtensions(extensions);
        if (!notSupported.isEmpty()) {
            throw new Error("Platform not supports extensions: " + String.join(", ", notSupported));
        }
    }
}
