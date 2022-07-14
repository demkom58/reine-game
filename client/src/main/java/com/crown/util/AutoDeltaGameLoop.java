package com.crown.util;

import com.crown.Game;

public class AutoDeltaGameLoop implements GameLoop {
    private final double secsPerFrame;
    private final double secsPerUpdate;

    private double lastLoopTime = 0D;
    private float accumulator = 0f;
    private float elapsedTime = 0f;

    private int updatesPerSecond;
    private int framesPerSecond;

    public AutoDeltaGameLoop() {
        this(60D, 30D);
    }

    public AutoDeltaGameLoop(double fps, double ups) {
        this.secsPerFrame = 1D / (fps / 1_000D);
        this.secsPerUpdate = 1D / (ups / 1_000D);
    }

    @Override
    public void init() {
        lastLoopTime = System.currentTimeMillis();
    }

    public void initIteration() {
        double time = System.currentTimeMillis();
        elapsedTime = (float) (time - lastLoopTime);
        lastLoopTime = time;
        accumulator += elapsedTime;
    }

    public boolean tryUpdate() {
        if (accumulator >= secsPerUpdate) {
            accumulator -= secsPerUpdate;
            return true;
        }

        return false;
    }

    public void sync() {
        double endTime = lastLoopTime + secsPerFrame;
        while (System.currentTimeMillis() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void loop(Game game) {
        long lastFlush = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        while (!game.getWindow().isShouldClose()) {
            initIteration();
            game.input();

            while (tryUpdate()) {
                game.update();
                updates++;
            }

            game.render((float) (elapsedTime / secsPerFrame));
            frames++;

            if (!game.getWindow().isVsync()) {
                sync();
            }

            if (System.currentTimeMillis() - lastFlush > 1000) {
                lastFlush = System.currentTimeMillis();

                this.updatesPerSecond = updates;
                this.framesPerSecond = frames;

                updates = frames = 0;
            }
        }
    }

    @Override
    public int getFps() {
        return framesPerSecond;
    }

    @Override
    public int getUps() {
        return updatesPerSecond;
    }
}
