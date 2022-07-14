package com.reine.client;

import com.crown.graphic.gl.shader.GlShader;
import com.crown.graphic.gl.shader.GlShaderProgram;
import com.crown.graphic.gl.shader.ShaderType;
import com.reine.block.Block;
import com.reine.client.render.Renderer;
import com.reine.client.render.chunk.ChunkRenderer;
import com.reine.client.render.chunk.mesh.RenderChunk;
import com.reine.client.save.minecaft.anvil.AnvilLoader;
import com.reine.client.util.ReineGame;
import com.reine.world.chunk.ChunkGrid;
import com.reine.world.chunk.IChunk;
import org.jglrxavpok.hephaistos.mca.AnvilException;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Client extends ReineGame {

    GlShaderProgram chunkShader;
    GlShaderProgram simpleShader;

    Renderer renderer = new Renderer();
    TextureManager textureManager = new TextureManager();
    ChunkRenderer chunkRenderer;

    ChunkGrid chunkGrid = new ChunkGrid();

    public Client() {
        super(400, 300, "Reine", 60, 30);
        camera.setZFar(5000);
        camera.moveY(-100);
    }

    @Override
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

        try (GlShader vertex = new GlShader(ShaderType.VERTEX, getClass().getResource("/shader/chunk.vsh"),
                "#version 460 core",
                "#define BATCH_CHUNK_COUNT " + RenderChunk.RENDER_CHUNK_SIZE);
             GlShader fragment = new GlShader(ShaderType.FRAGMENT, getClass().getResource("/shader/chunk.fsh"),
                     "#version 460 core",
                     "#define TEXTURES_COUNT " + textureManager.texturesCount()
             )) {
            chunkShader = new GlShaderProgram(vertex, fragment);
        }

        try (GlShader vertex = new GlShader(ShaderType.VERTEX, getClass().getResource("/shader/simple.vsh"),
                "#version 460 core");
             GlShader fragment = new GlShader(ShaderType.FRAGMENT, getClass().getResource("/shader/simple.fsh"),
                     "#version 460 core")) {
            simpleShader = new GlShaderProgram(vertex, fragment);
        }

        File save = new File("saves/Drehmal v2.1.1 PRIMORDIAL");
        try (AnvilLoader anvilLoader = new AnvilLoader(save, Block.AIR)) {
            for (int x = -3; x < 3; x++) {
                for (int y = 0; y < 8; y++) {
                    for (int z = -3; z < 3; z++) {
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

        List<IChunk> notEmpty = chunkGrid.loadedChunks().stream().filter(c -> !c.isEmpty()).toList();
        System.out.println("Loaded not empty chunks: " + notEmpty.size());

        chunkGrid.loadedChunks().forEach(c -> chunkRenderer.setChunk(chunkGrid, c));

        Set<RenderChunk> renderChunks = chunkRenderer.getRenderChunks(notEmpty);
        int drawCalls = renderChunks.stream().mapToInt(c -> c.passes().size()).sum();
        System.out.println("Draw Calls: " + drawCalls + "; Render Chunks: " + renderChunks.size());

        window.show();
        window.focus();
        super.start();
    }

    private final Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f velocity = new Vector3f();

    private long lastPrint = 0;

    @Override
    public void input() {
        if (lastPrint + 1000 < System.currentTimeMillis()) {
            System.out.println(gameLoop.getFps() + " fps, " + gameLoop.getUps() + " ups");
            lastPrint = System.currentTimeMillis();
        }

        super.input();

        float rotZ = 0.0f;
        float rotY = 0.0f;
        float rotX = 0.0f;

        float camSpeed = 0.5f;
        if (keyboard.isKeyDown(GLFW_KEY_LEFT_CONTROL)) {
            camSpeed = 10.0f;
        }

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

        camera.rotate(rotX, rotY, rotZ);
    }

    float cosTime;

    @Override
    public void update() {
        double t = System.currentTimeMillis() / 1000d;
        cosTime = (float) Math.cos(t);
    }

    @Override
    public void render(float delta) {
        camera.update();

        glClearColor(cosTime, cosTime, cosTime, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        chunkShader.use();
        chunkShader.setUniformMatrix4fv(ChunkRenderer.VIEW_MATRIX_UNIFORM, false, camera.toViewMatrix());
        chunkShader.setUniformMatrix4fv(ChunkRenderer.PROJECTION_MATRIX_UNIFORM, false, camera.toProjectionMatrix());

        Collection<IChunk> chunks = chunkGrid.loadedChunks();
        chunkRenderer.render(camera, chunkShader, chunks);

        window.update();
    }
}
