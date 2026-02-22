package org.example.marksmangame.model;

import java.util.ArrayList;
import java.util.List;

public class Arrow {
    private double x;
    private double y;
    private boolean active = true;

    private final List<PositionIObserver> positionObservers = new ArrayList<>();

    public Arrow(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(double width) {
        x += 8;
        notifyPosition();

        if (x > width) {
            deactivate();
        }
    }

    public void deactivate() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public void addPositionObserver(PositionIObserver o) {
        positionObservers.add(o);
    }

    private void notifyPosition() {
        positionObservers.forEach(o -> o.onPositionChanged(x, y));
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
