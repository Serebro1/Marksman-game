package org.example.marksmangame.model;

public class Player {
    private String name;
    private int score;
    private int shots;
    private boolean ready;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.shots = 0;
        this.ready = false;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getShots() {
        return shots;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void addScore(int points) {
        score += points;
    }

    public void addShot() {
        shots++;
    }

    public void reset() {
        this.score = 0;
        this.shots = 0;
    }
}
