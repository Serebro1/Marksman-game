package org.example.marksmangame.controllers;

public class GameLoop extends Thread {
    private final Engine engine;
    private final double width, height;
    private final Object pauseLock = new Object();
    private boolean paused = false;

    public GameLoop(Engine engine, double width, double height) {
        this.engine = engine;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                synchronized (pauseLock) {
                    while (paused) {
                        pauseLock.wait();
                    }
                }
                engine.update(width, height);
                Thread.sleep(16); // ~60 FPS
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopLoop() {
        interrupt();
        resumeLoop();
    }

    public void pauseLoop() {
        paused = true;
    }

    public void resumeLoop() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notify();
        }
    }
}
