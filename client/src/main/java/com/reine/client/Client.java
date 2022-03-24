package com.reine.client;

import com.reine.client.gl.GraphicsLibrary;
import com.reine.client.gl.Window;

import static org.lwjgl.opengl.GL11.*;

public class Client implements AutoCloseable {
    private final Window window;

    public Client() {
        GraphicsLibrary.init();
        this.window = new Window(400, 300, "Reine");
        this.window.setResizeCallback(this::handleResize);
    }

    public void start() {
        loop();
    }

    private void loop() {
        window.show();

        while (!window.shouldClose()) {
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            window.update();
        }
    }

    private void handleResize(long window, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void close() {
        GraphicsLibrary.destroy();
    }
}
