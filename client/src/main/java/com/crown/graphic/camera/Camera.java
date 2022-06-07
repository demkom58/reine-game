package com.crown.graphic.camera;

import com.crown.output.window.Window;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.text.NumberFormat;

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
    protected float zNear;
    protected float zFar;
    protected float aspect = 1920f / 1080f;
    private final Plane[] planes;

    public Camera() {
        this(new Vector3f(0, 0, -3), new Quaternionf(), 0.001f, 100.0f);
    }

    public Camera(Vector3f position, Quaternionf rotation, float zNear, float zFar) {
        this.position = position;
        this.rotation = rotation;
        this.zNear = zNear;
        this.zFar = zFar;

        this.planes = new Plane[6];
        for (int i = 0; i < this.planes.length; i++) {
            this.planes[i] = new Plane();
        }
    }

    public void update() {
        _ONE_MAIN_MATRIX
                .rotate(rotation, _viewMatrix)
                .translate(position, _viewMatrix)
                .get(viewBuffer);

        Matrix4f projection = new Matrix4f().perspective(fov, aspect, zNear, zFar);
        projection.get(projectionBuffer);

        createPlanes(projection, _viewMatrix);
    }

    public float[] toViewMatrix() {
        return viewBuffer;
    }

    public float[] toProjectionMatrix() {
        return projectionBuffer;
    }

    public void onResize(Window window, int width, int height) {
        setAspect(width, height);
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

    public float getZNear() {
        return zNear;
    }

    public void setZNear(float zNear) {
        this.zNear = zNear;
    }

    public float getZFar() {
        return zFar;
    }

    public void setZFar(float zFar) {
        this.zFar = zFar;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public void setAspect(int width, int height) {
        aspect = (float) width / height;
    }

    public float getAspect() {
        return aspect;
    }

    private void createPlanes(Matrix4f projection, Matrix4f viewMatrix) {
        final Matrix4f m = projection.mul(viewMatrix);

        // left
        planes[0].normal.set(m.m30() + m.m00(), m.m31() + m.m01(), m.m32() + m.m02());
        planes[0].distance = m.m33() + m.m03();

        // right
        planes[1].normal.set(m.m30() - m.m00(), m.m31() - m.m01(), m.m32() - m.m02());
        planes[1].distance = m.m33() - m.m03();

        // bottom
        planes[2].normal.set(m.m30() + m.m10(), m.m31() + m.m11(), m.m32() + m.m12());
        planes[2].distance = m.m33() + m.m13();

        // top
        planes[3].normal.set(m.m30() - m.m10(), m.m31() - m.m11(), m.m32() - m.m12());
        planes[3].distance = m.m33() - m.m13();

        // near
        planes[4].normal.set(m.m30() + m.m20(), m.m31() + m.m21(), m.m32() + m.m22());
        planes[4].distance = m.m33() + m.m23();

        // far
        planes[5].normal.set(m.m30() - m.m20(), m.m31() - m.m21(), m.m32() - m.m22());
        planes[5].distance = m.m33() - m.m23();

        // normalize
        for (int i = 0; i < 6; i++) {
            float length = planes[i].normal.distance(.0f, .0f, .0f);
            planes[i].normal.div(length);
            planes[i].distance /= length;
        }
    }

    public boolean isBoxInFrustum(float x, float y, float z,
                                  float xs, float ys, float zs) {
        for (int i = 0; i < planes.length; i++) {
            final Plane plane = planes[i];
            final Vector3f normal = plane.normal;

            float halfX = xs / 2f;
            float halfY = ys / 2f;
            float halfZ = zs / 2f;

            final Vector3f point = new Vector3f(
                    x + halfX,
                    y + halfY,
                    z + halfZ
            ).add(
                    halfX * Math.signum(normal.x),
                    halfY * Math.signum(normal.y),
                    halfZ * Math.signum(normal.z)
            );

            if (plane.distance(point) < 0) {
//                System.out.println("out in " + i + " (" + plane.distance(point) + ") for xyz(" + x + ";" + y + ";" + z + ")");
                return false;
            }
        }

        return true;
    }

    public static class Plane {
        public Vector3f normal = new Vector3f();
        public float distance = 0;

        public float distance(float x, float y, float z) {
            return distance + this.normal.dot(x, y, z);
        }

        public float distance(Vector3f point) {
            return this.distance(point.x, point.y, point.z);
        }

        @Override
        public String toString() {
            return "Plane{" +
                    "normal=" + normal.toString(NumberFormat.getNumberInstance()) +
                    ", distance=" + distance +
                    '}';
        }
    }

    public Plane[] getPlanes() {
        return planes;
    }
}
