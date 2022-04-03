package com.crown.graphic;

import com.crown.output.window.Window;
import com.crown.util.CrownMath;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.glViewport;

public class Camera {
    private static final Vector3f CAMERA_UP = new Vector3f();

    protected final Matrix4f oneMainMatrix = CrownMath.createMatrix4fMainOnes();
    protected final Matrix4f viewMatrix = CrownMath.createMatrix4fMainOnes();

    protected final float[] projectionBuffer = new float[16];
    protected final float[] viewBuffer = new float[16];

    protected final Vector3f position;
    protected final Quaternionf rotation;

    protected float fov = (float) toRadians(45);
    protected float zNear = 0.1f;
    protected float zFar = 100.0f;

    public Camera() {
        this(new Vector3f(0, 0, -3), new Quaternionf());
    }

    public Camera(Vector3f position, Quaternionf rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    Vector3f vec = new Vector3f();
    public float[] toViewMatrix() {
        rotation.transform(position, )
        oneMainMatrix
                .rotate(rotation.conjugate(), viewMatrix)
                .translate(position, viewMatrix)
                .get(viewBuffer);
        return viewBuffer;
    }

    public float[] toProjectionMatrix() {
        return projectionBuffer;
    }

    public void onResize(Window window, int width, int height) {
        new Matrix4f()
                .perspective(fov, (float) width / height, zNear, zFar)
                .get(projectionBuffer);
        glViewport(0, 0, width, height);
    }

    public void move(Vector3f offset) {
        position.add(offset.rotate(rotation));
    }

    public void rotate(float angleX, float angleY, float angleZ) {
        rotation.rotateXYZ(angleX, angleY, angleZ);
    }
}
