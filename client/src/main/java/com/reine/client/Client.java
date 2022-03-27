package com.reine.client;

import com.crown.graphic.GraphicsLibrary;
import com.crown.graphic.Window;
import com.crown.graphic.shader.Shader;
import com.crown.graphic.shader.ShaderProgram;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Client implements AutoCloseable {
    private final Window window;

    float[] vertices = {
            // vertices       colors            texture uv
            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, // bottom left
            -0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // top left
    };

    int[] indices = { // note that we start from 0!
            0, 1, 3, // first triangle
            1, 2, 3 // second triangle
    };

    ShaderProgram program;
    int vbo = 0;
    int vao = 0;
    int ebo = 0;
    int texture = 0;

    public Client() {
        GraphicsLibrary.init();
        this.window = new Window(400, 300, "Reine");
        this.window.setResizeCallback(this::handleResize);
    }

    public void start() {
        stbi_set_flip_vertically_on_load(true);
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            ByteBuffer pixels = stbi_load("assets/textures/img.png", width, height, channels, 4);
            if (pixels == null) {
                throw new IllegalStateException("Failed to load texture!");
            }

            texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            glGenerateMipmap(GL_TEXTURE_2D);
            stbi_image_free(pixels);
        }

        Shader vertex = new Shader(getClass().getResource("/shader/vertex.glsl"), true);
        Shader fragment = new Shader(getClass().getResource("/shader/fragment.glsl"), false);
        program = new ShaderProgram(vertex, fragment);
        vertex.delete();
        fragment.delete();

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
        // vertex attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // color attribute
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        // tex coords attribute
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);

        loop();
    }

    private void loop() {
        window.show();

        while (!window.shouldClose()) {
            float color = (float) Math.cos(System.currentTimeMillis() / 1000d);

            glClearColor(color, color, color, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            program.use();
            program.setUniform1f("time", (System.currentTimeMillis() - 1_648_197_818_412L) / 1000f);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture);
            glBindVertexArray(vao);
//            glDrawArrays(GL_TRIANGLES, 0, 3);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

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
