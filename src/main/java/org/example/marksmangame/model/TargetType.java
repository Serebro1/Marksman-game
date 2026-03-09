package org.example.marksmangame.model;

public enum TargetType {

    NEAR(40, 2, 1),
    FAR(20, 4, 2);

    private final double radius;
    private final double speed;
    private final int points;

    TargetType(double radius, double speed, int points) {
        this.radius = radius;
        this.speed = speed;
        this.points = points;
    }

    public Target create(double x, double y) {
        return new Target(this, x, y);
    }

    public double getRadius() { return radius; }
    public double getSpeed() { return speed; }
    public int getPoints() { return points; }
}
