package org.example.marksmangame.dto;

import java.io.Serializable;

public class PlayerDTO implements Serializable {
    private int id;
    private String name;
    private int score;
    private int shots;
    private boolean ready;

    public PlayerDTO(int id, String name, int score, int shots, boolean ready) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.shots = shots;
        this.ready = ready;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public String toString() {
        return "PlayerDTO{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", shots=" + shots +
                ", ready=" + ready +
                '}';
    }
}
