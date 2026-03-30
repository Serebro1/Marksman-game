package org.example.marksmangame.server.game;

import org.example.marksmangame.dto.GameState;

public class GameStateController {
    private GameState state = GameState.WAITING;
    private String pausedBy = null;
    private String winnerName = null;

    public void start() {
        if (state == GameState.RUNNING) return;
        state = GameState.RUNNING;
        winnerName = null;
        pausedBy = null;
    }

    public void pause(String playerName) {
        if (state != GameState.RUNNING) return;
        state = GameState.PAUSED;
        pausedBy = playerName;
    }

    public void resume() {
        if (state != GameState.PAUSED) return;
        state = GameState.RUNNING;
        pausedBy = null;
    }

    public void stop() {
        if (state == GameState.WAITING) return;
        state = GameState.WAITING;
        pausedBy = null;
        winnerName = null;
    }

    public void finishGame(String winnerName) {
        this.winnerName = winnerName;
        this.state = GameState.WAITING;
        this.pausedBy = null;
    }

    public void removePlayer() {
        pausedBy = null;
        if (state == GameState.PAUSED) {
            state = GameState.WAITING;
        }
    }

    public void setWinner(String playerName) {
        this.winnerName = playerName;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public String consumeWinnerName() {
        String w = winnerName;
        winnerName = null;
        return w;
    }

    public GameState getState() {
        return state;
    }

    public String getPausedBy() {
        return pausedBy;
    }
}
