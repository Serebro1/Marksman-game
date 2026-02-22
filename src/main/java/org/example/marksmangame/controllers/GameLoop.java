package org.example.marksmangame.controllers;

public class GameLoop extends Thread {
    private final Engine engine;
    private final double width, height;
    private boolean running = true;

    public GameLoop(Engine engine, double width, double height) {
        this.engine = engine;
        this.width = width;
        this.height = height;
    }

    public void stopLoop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            engine.update(width, height);

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }
}
