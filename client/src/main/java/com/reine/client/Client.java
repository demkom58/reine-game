package com.reine.client;

import com.crown.graphic.CrownGame;
import com.crown.graphic.shader.Shader;
import com.crown.graphic.shader.ShaderProgram;
import com.crown.graphic.texture.Texture2D;
import com.crown.input.keyboard.Keyboard;
import com.crown.input.mouse.Mouse;
import com.crown.output.window.Window;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Client extends CrownGame {
    private final Matrix4f oneMainMatrix = new Matrix4f().identity();
    private final float[] modelBuffer = new float[4 * 4];
    private final Matrix4f modelMatrix = new Matrix4f().identity();


    float[] vertices = {
            // vertices         texture uv
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
    };

//    int[] indices = { // note that we start from 0!
//            0, 1, 3, // first triangle
//            1, 2, 3 // second triangle
//    };

    ShaderProgram program;
    Texture2D texture;
    int vbo = 0;
    int vao = 0;
//    int ebo = 0;

    Mouse mouse;
    Keyboard keyboard;

    public Client() {
        super(400, 300, "Reine");

        mouse = new Mouse(window);
        keyboard = new Keyboard(window);

        mouse.setPositionCallback(this::onCursorMove);
        window.setFocusCallback(this::onFocus);
    }

    public void start() {
        texture = new Texture2D("assets/textures/img.png");

        try (Shader vertex = new Shader(getClass().getResource("/shader/vertex.glsl"), true);
             Shader fragment = new Shader(getClass().getResource("/shader/fragment.glsl"), false)) {
            program = new ShaderProgram(vertex, fragment);
        }

        // create VAO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // bind to VAO vertices using VBO
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

//        // bind to VAO indices using EBO
//        ebo = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // configure attribute
        // vertex attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // tex coords attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        loop();
    }

    float cosTime;
    Vector3f modelPos = new Vector3f();
    Vector3f modelScale = new Vector3f(1f);
    Quaternionf modelRotation = new Quaternionf().rotateXYZ(45, 0, 0);

    private void loop() {
        window.show();

        while (!window.shouldClose()) {
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
            System.exit(0);
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
        oneMainMatrix
                .translate(modelPos, modelMatrix)
                .scale(modelScale, modelMatrix)
                .rotateAffine(modelRotation, modelMatrix)
                .get(modelBuffer);

        program.setUniformMatrix4fv("model", false, modelBuffer);
        program.setUniformMatrix4fv("view", false, camera.toViewMatrix());
        program.setUniformMatrix4fv("projection", false, camera.toProjectionMatrix());

        texture.use(0);
        glBindVertexArray(vao);
        glEnable(GL_DEPTH_TEST);
        glDrawArrays(GL_TRIANGLES, 0, vertices.length / 5);
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);

        window.update();
    }
}
