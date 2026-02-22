package org.example.marksmangame.controllers;

import org.example.marksmangame.model.*;

import java.util.ArrayList;
import java.util.List;

public class Engine {

    private GameState state = GameState.STOPPED;

    private final Player player = new Player();
    private final List<Target> targets = new ArrayList<>();
    private Arrow arrow;

    private final List<ArrowCreatedIObserver> createdObservers = new ArrayList<>();
    private final List<ArrowDestroyedIObserver> destroyedObservers = new ArrayList<>();

    public void start() {
        player.reset();
        targets.clear();
        arrow = null;

        targets.add(new Target(TargetType.NEAR, 700, 300));
        targets.add(new Target(TargetType.FAR, 850, 300));

        state = GameState.RUNNING;
    }

    public void stop() {
        state = GameState.STOPPED;
    }

    public void pause() {
        if (state == GameState.RUNNING)
            state = GameState.PAUSED;
    }

    public void resume() {
        if (state == GameState.PAUSED)
            state = GameState.RUNNING;
    }

    public void shoot() {
        if (state != GameState.RUNNING || arrow != null) return;

        player.addShot();
        arrow = new Arrow(150, 300, 8);

        createdObservers.forEach(o -> o.onArrowCreated(arrow));
    }

    public void update(double width, double height) {
        if (state != GameState.RUNNING) return;

        targets.forEach(t -> t.move(height));

        if (arrow != null) {
            arrow.move(width);

            for (Target t : targets) {
                if (arrow.isActive() &&
                        Math.abs(arrow.getX() - t.getX()) < t.getRadius() &&
                        Math.abs(arrow.getY() - t.getY()) < t.getRadius()) {

                    player.addScore(t.getPoints());
                    arrow.deactivate();
                }
            }

            if (!arrow.isActive()) {
                arrow = null;
                destroyedObservers.forEach(ArrowDestroyedIObserver::onArrowDestroyed);
            }
        }
    }

    public Player getPlayer() { return player; }
    public List<Target> getTargets() { return targets; }

    public void addArrowCreatedObserver(ArrowCreatedIObserver o) {
        createdObservers.add(o);
    }

    public void addArrowDestroyedObserver(ArrowDestroyedIObserver o) {
        destroyedObservers.add(o);
    }
}
