package org.example.marksmangame.dto;

import java.io.Serializable;

public class ArrowDTO implements Serializable {
    private double x, y;
    private int playerId;
    private boolean active;

    public ArrowDTO(double x, double y, int playerId, boolean active) {
        this.x = x;
        this.y = y;
        this.playerId = playerId;
        this.active = active;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "ArrowDTO{" +
                "x=" + x +
                ", y=" + y +
                ", playerId=" + playerId +
                ", active=" + active +
                '}';
    }
}
