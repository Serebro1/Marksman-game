package org.example.marksmangame.model;

public class Target {
    private double x, y;
    private final double radius;
    private final double speed;
    private final int points;

    public Target(TargetType type, double x, double y) {
        this.x = x;
        this.y = y;

        if (type == TargetType.NEAR) {
            radius = 40;
            speed = 2;
            points = 1;
        } else {
            radius = 20;
            speed = 4;
            points = 2;
        }
    }

    public void move(double height) {
        y += speed;
        if (y > height) y = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }

    public int getPoints() {
        return points;
    }
}
