package com.crown.graphic.util;

import com.crown.graphic.unit.Mesh;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public final class Draw {
    public static void line(Vector3f start, Vector3f end, Vector3f color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer coords = stack.mallocFloat(6);
            FloatBuffer colors = stack.mallocFloat(6);

            coords
                    .put(start.x).put(start.y).put(start.z)
                    .put(end.x).put(end.y).put(end.z);

            colors
                    .put(color.x).put(color.y).put(color.z)
                    .put(color.x).put(color.y).put(color.z);

            try (Mesh mesh = Mesh.builder(GL_LINES)
                    .positions(0, coords.flip(), 3, false)
                    .attribute(1, colors.flip(), 3, false)
                    .build()
            ) {
                mesh.bind();
                mesh.draw();
            }
        }
    }

    public static void line(Vector3f end, Vector3f color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer coords = stack.mallocFloat(6);
            FloatBuffer colors = stack.mallocFloat(6);

            coords
                    .put(0.f).put(0.f).put(0.f)
                    .put(end.x).put(end.y).put(end.z);

            colors
                    .put(color.x).put(color.y).put(color.z)
                    .put(color.x).put(color.y).put(color.z);

            try (Mesh mesh = Mesh.builder(GL_LINES)
                    .positions(0, coords.flip(), 3, false)
                    .attribute(1, colors.flip(), 3, false)
                    .build()
            ) {
                mesh.bind();
                mesh.draw();
            }
        }
    }
}
