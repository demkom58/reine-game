package com.reine.client;

import com.crown.graphic.CrownGame;
import com.crown.graphic.shader.Shader;
import com.crown.graphic.shader.ShaderProgram;
import com.crown.graphic.texture.TextureManager;
import com.crown.graphic.unit.Mesh;
import com.crown.input.keyboard.Keyboard;
import com.crown.input.mouse.Mouse;
import com.crown.output.window.Window;
import com.reine.block.Block;
import com.reine.client.render.block.BlockModelManager;
import com.reine.world.chunk.Chunk;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.SimpleChunk;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.util.Collection;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Client extends CrownGame {
    private final Matrix4f oneMainMatrix = new Matrix4f().identity();
    private final float[] modelBuffer = new float[4 * 4];
    private final Matrix4f modelMatrix = new Matrix4f().identity();

    ShaderProgram program;

    Mouse mouse;
    Keyboard keyboard;

    TextureManager textureManager = new TextureManager();
    BlockModelManager blockModelManager = new BlockModelManager();

    ChunkGrid chunkGrid = new ChunkGrid();

    public Client() {
        super(400, 300, "Reine");

        mouse = new Mouse(window);
        keyboard = new Keyboard(window);

        mouse.setPositionCallback(this::onCursorMove);
        window.setFocusCallback(this::onFocus);
    }

    public void start() {
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

        textureManager.buildAtlas();
        blockModelManager.reload(textureManager);

        try (Shader vertex = new Shader(getClass().getResource("/shader/vertex.vsh"), true);
             Shader fragment = new Shader(getClass().getResource("/shader/fragment.fsh"), false)) {
            program = new ShaderProgram(vertex, fragment);
        }

        for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
                for (int z = 0; z < 64; z++) {
                    chunkGrid.setBlockId(x, y, z, Block.BOOKSHELF.getId());
                }
            }
        }

        loop();
    }

    float cosTime;
    Quaternionf modelRotation = new Quaternionf().rotateXYZ(45, 0, 0);

    private void loop() {
        window.show();
        window.focus();

        while (!window.isShouldClose()) {
            handleInput();
            update();
            render();
        }
    }

    private final Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f velocity = new Vector3f();

    private void handleInput() {
        final float camSpeed = 0.05f;
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
        glClearColor(cosTime, cosTime, cosTime, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        program.use();
        program.setUniformMatrix4fv("view", false, camera.toViewMatrix());
        program.setUniformMatrix4fv("projection", false, camera.toProjectionMatrix());

        textureManager.getAtlas().use(0);

        glEnable(GL_DEPTH_TEST);

        Collection<SimpleChunk> chunks = chunkGrid.loadedChunks();


        for (SimpleChunk chunk : chunks) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        int blockId = chunk.getBlockId(x, y, z);
                        if (blockId == 0) {
                            continue;
                        }

                        oneMainMatrix
                                .translate(
                                        chunk.getX() * Chunk.CHUNK_WIDTH + x,
                                        chunk.getY() * Chunk.CHUNK_HEIGHT + y,
                                        chunk.getZ() * Chunk.CHUNK_LENGTH + z,
                                        modelMatrix
                                ).get(modelBuffer);
                        program.setUniformMatrix4fv("model", false, modelBuffer);

                        Mesh mesh = blockModelManager.getMesh(blockId);
                        mesh.bind();
                        mesh.draw();
                    }
                }
            }
        }

        glBindVertexArray(0);

        window.update();
    }
}
