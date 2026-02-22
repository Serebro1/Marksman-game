package org.example.marksmangame.model;

@FunctionalInterface
public interface ShotsIObserver {
    void onShotsChanged(int newShots);
}
