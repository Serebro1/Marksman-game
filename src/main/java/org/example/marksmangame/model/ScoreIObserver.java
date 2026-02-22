package org.example.marksmangame.model;

@FunctionalInterface
public interface ScoreIObserver {
    void onScoreChanged(int newScore);
}
