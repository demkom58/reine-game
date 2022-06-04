package com.crown.util;

import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class UpdateCounter {
    private final Consumer<UpdateCounter> onTimePassed;
    private final double period;

    private double lastTime = glfwGetTime();
    private int updates = 0;

    public UpdateCounter(Consumer<UpdateCounter> onTimePassed) {
        this(onTimePassed, 1.0d);
    }

    public UpdateCounter(Consumer<UpdateCounter> onTimePassed, double period) {
        this.onTimePassed = onTimePassed;
        this.period = period;
    }

    public void update() {
        double currentTime = glfwGetTime();
        updates++;
        if (currentTime - lastTime >= period) {
            onTimePassed.accept(this);
            updates = 0;
            lastTime += period;
        }
    }

    public int getUpdates() {
        return updates;
    }

    public float getAverageTime() {
        return 1000.0f / updates;
    }
}
