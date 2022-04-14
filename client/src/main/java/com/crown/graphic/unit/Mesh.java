package com.crown.graphic.unit;

import com.crown.graphic.texture.Texture2D;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Mesh {
    private final int vaoId;
    private final int posVboId;
    private final int idxVboId;
    private final int texVboId;
    private final int vertexCount;

    public Mesh(float[] positions) {
        texVboId = -1;
        idxVboId = -1;
        vertexCount = positions.length / 3;

        try (MemoryStack stack = stackPush()) {
            FloatBuffer verticesBuffer = stack.mallocFloat(positions.length)
                    .put(positions)
                    .flip();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            posVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public Mesh(float[] positions, int[] indices) {
        texVboId = -1;
        vertexCount = indices.length;

        try (MemoryStack stack = stackPush()) {
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            FloatBuffer verticesBuffer = stack.mallocFloat(positions.length)
                    .put(positions)
                    .flip();

            posVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            IntBuffer indicesBuffer = stack.mallocInt(indices.length)
                    .put(indices)
                    .flip();

            idxVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public Mesh(float[] positions, float[] texCoords) {
        idxVboId = -1;
        vertexCount = positions.length / 3;

        try (MemoryStack stack = stackPush()) {
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            final var verticesBuffer = stack.mallocFloat(positions.length).put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId = glGenBuffers());
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);

            final var texCoordsBuffer = stack.mallocFloat(texCoords.length).put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, texVboId = glGenBuffers());
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(1);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public Mesh(float[] positions, int[] indices, float[] texCoords) {
        vertexCount = indices.length;

        try (MemoryStack stack = stackPush()) {
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            final var verticesBuffer = stack.mallocFloat(positions.length).put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId = glGenBuffers());
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);

            final var indicesBuffer = stack.mallocInt(indices.length).put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId = glGenBuffers());
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            final var texCoordsBuffer = stack.mallocFloat(texCoords.length).put(texCoords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, texVboId = glGenBuffers());
            glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(1);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public void bind() {
        glBindVertexArray(vaoId);
    }

    public void draw() {
        if (idxVboId == -1) {
            glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        } else {
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getPosVboId() {
        return posVboId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getIdxVboId() {
        return idxVboId;
    }

    public int getTexVboId() {
        return texVboId;
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDeleteBuffers(posVboId);
        if (idxVboId != -1) {
            glDeleteBuffers(idxVboId);
        }
        if (texVboId != -1) {
            glDeleteBuffers(texVboId);
        }

        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
