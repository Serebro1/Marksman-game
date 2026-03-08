package org.example.marksmangame.dto;

import java.io.Serializable;

public class TargetDTO implements Serializable {
    private int id;
    private double x, y;
    private double radius;
    private boolean active;

    public TargetDTO(int id, double x, double y, double radius, boolean active) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "TargetDTO{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", radius=" + radius +
                ", active=" + active +
                '}';
    }
}
