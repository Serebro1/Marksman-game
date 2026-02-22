package org.example.marksmangame.model;

@FunctionalInterface
public interface PositionIObserver {
    void onPositionChanged(double x, double y);
}
