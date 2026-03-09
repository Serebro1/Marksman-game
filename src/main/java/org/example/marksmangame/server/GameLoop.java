package org.example.marksmangame.server;

import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.GameStateDTO;

public class GameLoop extends Thread {
    private final Engine engine;
    private final GameServer server;
    private volatile boolean running = true;
    private volatile boolean paused = false;
    private final Object pauseLock = new Object();

    public GameLoop(Engine engine, GameServer server) {
        this.engine = engine;
        this.server = server;
    }

    public void pauseLoop() {
        paused = true;
    }

    public void resumeLoop() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                while (paused && running) {
                    try { pauseLock.wait(); } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            if (!running) break;
            if (engine.getState() == GameState.RUNNING) {
                engine.update();
                GameStateDTO state = engine.getCurrentState();
                server.broadcast(state);
            }
            try { Thread.sleep(16); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
