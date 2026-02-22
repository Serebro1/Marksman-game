package org.example.marksmangame.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int score, shots;
    private final List<ScoreIObserver> scoreIObserverList = new ArrayList<>();
    private final List<ShotsIObserver> shotsIObserverList = new ArrayList<>();

    public void reset() {
        score = 0;
        shots = 0;
        notifyScore();
        notifyShots();
    }

    public void addScore(int value) {
        score += value;
        notifyScore();
    }

    public void addShot() {
        shots++;
        notifyShots();
    }

    public void addScoreObserver(ScoreIObserver o) {
        scoreIObserverList.add(o);
    }
    public void addShotsObserver(ShotsIObserver o) {
        shotsIObserverList.add(o);
    }
    private void notifyScore() {
        scoreIObserverList.forEach(o -> o.onScoreChanged(score));
    }
    private void notifyShots() {
        shotsIObserverList.forEach(o -> o.onShotsChanged(shots));
    }
}
