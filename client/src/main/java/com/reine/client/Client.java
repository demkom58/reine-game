package com.reine.client;

import com.reine.client.gl.GraphicsLibrary;
import com.reine.client.gl.Window;
import com.reine.client.gl.shader.Shader;
import com.reine.client.gl.shader.ShaderProgram;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Client implements AutoCloseable {
    private final Window window;

    float[] vertices = {
            // first triangle
            0.5f, 0.5f, 0.0f, // top right
            0.5f, -0.5f, 0.0f, // bottom right
            -0.5f, 0.5f, 0.0f, // top left
// second triangle
    };

    float[] vertices2 = {
            0.5f, -0.5f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f, // bottom left
            -0.5f, 0.5f, 0.0f // top left
    };

    int[] indices = { // note that we start from 0!
            0, 1, 3, // first triangle
            1, 2, 3 // second triangle
    };

    ShaderProgram program;
    ShaderProgram program2;
    int vbo = 0;
    int vao = 0;
    int ebo = 0;

    int vbo2 = 0;
    int vao2 = 0;

    public Client() {
        GraphicsLibrary.init();
        this.window = new Window(400, 300, "Reine");
        this.window.setResizeCallback(this::handleResize);
    }

    public void start() {
        Shader vertex = new Shader(getClass().getResource("/shader/vertex.glsl"), true);
        Shader fragment = new Shader(getClass().getResource("/shader/fragment.glsl"), false);
        Shader fragment2 = new Shader(getClass().getResource("/shader/fragment2.glsl"), false);
        program = new ShaderProgram(vertex, fragment);
        program2 = new ShaderProgram(vertex, fragment2);
        vertex.delete();
        fragment.delete();
        fragment2.delete();

        // create VAO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // bind to VAO vertices using VBO
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // bind to VAO indices using EBO
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // configure attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, NULL);
        glEnableVertexAttribArray(0);

        // create vao
        vao2 = glGenVertexArrays();
        glBindVertexArray(vao2);

        // create vbo for vertices
        vbo2 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo2);
        glBufferData(GL_ARRAY_BUFFER, vertices2, GL_STATIC_DRAW);

        // configure attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, NULL);
        glEnableVertexAttribArray(0);


        loop();
    }

    private void loop() {
        window.show();

        while (!window.shouldClose()) {
            float color = (float) Math.cos(System.currentTimeMillis() / 1000d);

            glClearColor(color, color, color, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            program.use();

            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

            program2.use();
            glBindVertexArray(vao2);
            glDrawArrays(GL_TRIANGLES, 0, 3);

            glBindVertexArray(0);

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
