package org.example.marksmangame.model;

public class Arrow {
    private double x, y;
    private final double speed;
    private boolean active = true;
    private final Player owner;

    public Arrow(Player owner, double x, double y, double speed) {
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void move(double width) {
        x += speed;
        if (x > width) { active = false; }
    }

    public void deactivate() { active = false; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isActive() { return active; }
    public Player getOwner() { return owner; }
}
