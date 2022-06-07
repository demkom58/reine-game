package com.reine.client;

import com.crown.graphic.CrownGame;
import com.crown.graphic.GraphicsLibrary;
import com.crown.graphic.shader.Shader;
import com.crown.graphic.shader.ShaderProgram;
import com.crown.graphic.texture.TextureManager;
import com.crown.input.keyboard.Keyboard;
import com.crown.input.mouse.Mouse;
import com.crown.output.window.Window;
import com.crown.util.UpdateCounter;
import com.reine.client.render.Renderer;
import com.reine.client.render.chunk.ChunkRenderer;
import com.reine.client.save.minecaft.anvil.AnvilLoader;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.IChunk;
import org.jetbrains.annotations.NotNull;
import org.jglrxavpok.hephaistos.mca.AnvilException;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Client extends CrownGame {
    private static final String[] REQUIRED_EXTENSIONS = {
            "GL_ARB_gpu_shader_int64",
            "GL_ARB_bindless_texture",
    };

    ShaderProgram chunkShader;
    ShaderProgram simpleShader;

    Mouse mouse;
    Keyboard keyboard;

    Renderer renderer = new Renderer();
    TextureManager textureManager = new TextureManager();
    ChunkRenderer chunkRenderer;

    ChunkGrid chunkGrid = new ChunkGrid();

    public Client() {
        super(400, 300, "Reine");

        Set<String> notSupported = GraphicsLibrary.isNotSupportedExtensions(REQUIRED_EXTENSIONS);
        if (!notSupported.isEmpty()) {
            throw new Error("Platform not supports extensions: " + String.join(", ", notSupported));
        }

        mouse = new Mouse(window);
        keyboard = new Keyboard(window);

        mouse.setPositionCallback(this::onCursorMove);
        window.setFocusCallback(this::onFocus);

        camera.setZFar(1000);
        camera.setAspect(400, 300);
        camera.moveY(-100);

        GraphicsLibrary.enableMultiSampling();
        window.setSampling(GLFW_SAMPLES, 4);
    }

    public void start() throws RuntimeException {
        File texturesDirectory = new File("assets/textures/");
        File[] textures = texturesDirectory.listFiles();
        if (textures == null) {
            throw new Error("No textures!");
        }

        for (File tex : textures) {
            if (tex.isDirectory()) {
                continue;
            }

            textureManager.registerTexture(tex.getName());
        }

        textureManager.rebuild();
        chunkRenderer = new ChunkRenderer(renderer, textureManager);

        try (Shader vertex = new Shader(getClass().getResource("/shader/chunk.vsh"), true,
                "#version 450 core");
             Shader fragment = new Shader(getClass().getResource("/shader/chunk.fsh"), false,
                     "#version 450 core",
                     "#define TEXTURES_COUNT " + textureManager.texturesCount())) {
            chunkShader = new ShaderProgram(vertex, fragment);
        }

        try (Shader vertex = new Shader(getClass().getResource("/shader/simple.vsh"), true,
                "#version 450 core");
             Shader fragment = new Shader(getClass().getResource("/shader/simple.fsh"), false,
                     "#version 450 core")) {
            simpleShader = new ShaderProgram(vertex, fragment);
        }


        File save = new File("saves/Drehmal v2.1.1 PRIMORDIAL");
        try (AnvilLoader anvilLoader = new AnvilLoader(save)) {
            for (int x = 0; x < 25; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 25; z++) {
                        try {
                            IChunk iChunk = anvilLoader.loadChunk(x, y, z);
                            chunkGrid.setChunk(x, y, z, iChunk);
                        } catch (AnvilException | IOException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }

            System.out.println("Invalid loads stats: ");
            anvilLoader.getInvalidStatistics().forEach((k, v) -> System.out.println(k + ": " + v));
        } catch (Exception e) {
            e.printStackTrace();
        }

        chunkGrid.loadedChunks().forEach(c -> chunkRenderer.setChunk(chunkGrid, c));
        loop();
    }

    float cosTime;
    Quaternionf modelRotation = new Quaternionf();
    UpdateCounter updateCounter;

    private void loop() {
        updateCounter = new UpdateCounter(c ->
                System.out.println(c.getAverageTime() + "ms/frame ~ " + c.getUpdates() + " fps"));

        window.show();
        window.focus();

        while (!window.isShouldClose()) {
            updateCounter.update();

            handleInput();
            update();
            render();
        }
    }

    private final Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f velocity = new Vector3f();

    private void handleInput() {
        final float camSpeed = 0.5f;
        float rotZ = 0.0f;
        float rotY = 0.0f;
        float rotX = 0.0f;

        if (keyboard.isKeyDown(GLFW_KEY_W)) {
            front.mul(-camSpeed, velocity);
            camera.move(velocity);
        }

        if (keyboard.isKeyDown(GLFW_KEY_S)) {
            front.mul(camSpeed, velocity);
            camera.move(velocity);
        }

        if (keyboard.isKeyDown(GLFW_KEY_A)) {
            front.cross(up, velocity).mul(camSpeed, velocity);
            camera.move(velocity);
        }

        if (keyboard.isKeyDown(GLFW_KEY_D)) {
            front.cross(up, velocity).mul(-camSpeed, velocity);
            camera.move(velocity);
        }

        if (keyboard.isKeyDown(GLFW_KEY_Q)) {
            rotZ -= 0.1f;
        }

        if (keyboard.isKeyDown(GLFW_KEY_E)) {
            rotZ += 0.1f;
        }

        if (keyboard.isKeyDown(GLFW_KEY_DOWN)) {
            rotY += 0.1f;
        }

        if (keyboard.isKeyDown(GLFW_KEY_UP)) {
            rotY -= 0.1f;
        }

        if (keyboard.isKeyDown(GLFW_KEY_RIGHT)) {
            rotX += 0.1f;
        }

        if (keyboard.isKeyDown(GLFW_KEY_LEFT)) {
            rotX -= 0.1f;
        }

        if (keyboard.isKeyDown(GLFW_KEY_ESCAPE)) {
            window.setShouldClose(true);
        }

        if (keyboard.isKeyDown(GLFW_KEY_LEFT_ALT)) {
            mouse.ungrabMouseCursor();
        }

        camera.rotate(rotX, rotY, rotZ);
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

    private void update() {
        double t = System.currentTimeMillis() / 1000d;
        cosTime = (float) Math.cos(t);

        float r = cosTime / 50f;
        modelRotation.rotateXYZ(r, (float) Math.cos(r) / 50f, r);
    }

    private void render() {
        camera.update();

        glClearColor(cosTime, cosTime, cosTime, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        chunkShader.use();
        chunkShader.setUniformMatrix4fv("view", false, camera.toViewMatrix());
        chunkShader.setUniformMatrix4fv("projection", false, camera.toProjectionMatrix());

        Collection<IChunk> chunks = chunkGrid.loadedChunks();
        chunkRenderer.render(camera, chunkShader, chunks);

        window.update();
    }
}
