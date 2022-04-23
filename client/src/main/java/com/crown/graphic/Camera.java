package com.crown.graphic;

import com.crown.output.window.Window;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static java.lang.Math.toRadians;
import static org.lwjgl.opengl.GL11.glViewport;

public class Camera {
    protected static final Matrix4f _ONE_MAIN_MATRIX = new Matrix4f().identity();

    protected final Matrix4f _viewMatrix = new Matrix4f().identity();
    protected final Vector3f _moveVec = new Vector3f();

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

    public float[] toViewMatrix() {
        _ONE_MAIN_MATRIX
                .rotate(rotation, _viewMatrix)
                .translate(position, _viewMatrix)
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
        position.add(rotation.positiveX(_moveVec).mul(offset.x))
                .add(rotation.positiveY(_moveVec).mul(offset.y))
                .add(rotation.positiveZ(_moveVec).mul(offset.z));
    }

    public void moveX(float offset) {
        position.add(rotation.positiveX(_moveVec).mul(offset));
    }

    public void moveY(float offset) {
        position.add(rotation.positiveY(_moveVec).mul(offset));
    }

    public void moveZ(float offset) {
        position.add(rotation.positiveZ(_moveVec).mul(offset));
    }

    public void setRotation(float angle, float x, float y, float z) {
        rotation.setAngleAxis(angle, x, y, z);
    }

    public void rotate(float angleX, float angleY, float angleZ) {
        rotation.rotateLocalY(angleX)
                .rotateLocalX(angleY)
                .rotateLocalZ(angleZ);
    }

    public void rotateX(float angleX) {
        rotation.rotateLocalY(angleX);
    }

    public void rotateY(float angleY) {
        rotation.rotateLocalX(angleY);
    }

    public void rotateZ(float angleZ) {
        rotation.rotateLocalZ(angleZ);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }
}
