package org.example.marksmangame.server.game;

import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.server.game.model.Arrow;
import org.example.marksmangame.server.game.model.Player;
import org.example.marksmangame.server.game.model.Target;
import org.example.marksmangame.server.game.model.TargetType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameMechanics {
    private final List<Target> targets = new ArrayList<>();
    private final List<Arrow> arrows = new ArrayList<>();

    // Константы, необходимые для расчётов (передаются из Engine)
    private final double height;
    private final double width;
    private final double arrowSpeed;
    private final double arrowStartX;
    private final double[] playerStartY;
    private final int winScore;

    public GameMechanics(double height, double width, double arrowSpeed,
                         double arrowStartX, double[] playerStartY, int winScore) {
        this.height = height;
        this.width = width;
        this.arrowSpeed = arrowSpeed;
        this.arrowStartX = arrowStartX;
        this.playerStartY = playerStartY.clone();
        this.winScore = winScore;
    }

    public void createDefaultTargets() {
        targets.clear();
        targets.add(TargetType.NEAR.create(700, 300));
        targets.add(TargetType.FAR.create(850, 300));
    }

    public void shoot(Player player, int playerIndex) {
        boolean alreadyShot = arrows.stream()
                .anyMatch(a -> a.isActive() && a.getOwner().equals(player));
        if (alreadyShot) return;

        if (playerIndex >= 0 && playerIndex < playerStartY.length) {
            Arrow arrow = new Arrow(player, arrowStartX, playerStartY[playerIndex], arrowSpeed);
            arrows.add(arrow);
            player.addShot();
        }
    }

    public void update(PlayerManager playerManager, GameStateController stateController) {
        if (stateController.getState() != GameState.RUNNING) return;

        for (Target t : targets) {
            t.move(height);
        }

        Iterator<Arrow> it = arrows.iterator();
        while (it.hasNext()) {
            Arrow a = it.next();
            if (!a.isActive()) {
                it.remove();
                continue;
            }
            a.move(width);
            if (!a.isActive()) {
                it.remove();
                continue;
            }

            for (Target t : targets) {
                double dx = a.getX() - t.getX();
                double dy = a.getY() - t.getY();
                if (Math.hypot(dx, dy) < t.getRadius()) {
                    a.getOwner().addScore(t.getPoints());
                    a.deactivate();
                    break;
                }
            }
        }

        for (Player p : playerManager.getPlayers()) {
            if (p.getScore() >= winScore) {
                stateController.finishGame(p.getName());
                arrows.clear();
                playerManager.getPlayers().forEach(pl -> pl.setReady(false));
                break;
            }
        }
    }

    public void clearArrowsAndTargets() {
        arrows.clear();
        targets.clear();
    }

    public List<Target> getTargets() {
        return targets;
    }

    public List<Arrow> getArrows() {
        return arrows;
    }
}
