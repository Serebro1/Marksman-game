package org.example.marksmangame.server.game.model;

public class Target {
    private double x, y;
    private final TargetType type;

    public Target(TargetType type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public void move(double height) {
        y += type.getSpeed();
        if (y > height) y = 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getRadius() { return type.getRadius(); }
    public int getPoints() { return type.getPoints(); }
}
