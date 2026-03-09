package org.example.marksmangame.server;

import org.example.marksmangame.dto.*;
import org.example.marksmangame.model.*;

import java.util.*;

public class Engine {
    private static final double HEIGHT = 600;
    private static final double WIDTH  = 850;
    private static final double ARROW_SPEED = 8;
    private static final double ARROW_START_X = 150;
    private static final double[] PLAYER_START_Y = {200, 300, 400, 500};
    private static final int WIN_SCORE = 6;

    private final List<Player> players = new ArrayList<>();
    private final List<Target> targets = new ArrayList<>();
    private final List<Arrow> arrows = new ArrayList<>();

    private GameState state = GameState.WAITING;
    private String pausedBy = null;
    private String winnerName = null;


    public synchronized void addPlayer(Player p) { players.add(p); }

    public synchronized void removePlayer(Player p) {
        players.remove(p);
        arrows.removeIf(a -> a.getOwner() == p);
        if (Objects.equals(p.getName(), pausedBy)) resume();
    }

    public synchronized void setPlayerReady(String playerName, boolean ready) {
        findPlayer(playerName).ifPresent(p -> p.setReady(ready));
    }

    public synchronized boolean allPlayersReady() {
        return !players.isEmpty() && players.stream().allMatch(Player::isReady);
    }

    public synchronized void start() {
        if (state == GameState.RUNNING) return;
        players.forEach(Player::reset);
        arrows.clear();
        targets.clear();
        createDefaultTargets();
        state = GameState.RUNNING;
        winnerName = null;
    }

    private void createDefaultTargets() {
        targets.add(TargetType.NEAR.create(700, 300));
        targets.add(TargetType.FAR.create(850, 300));
    }

    public synchronized void pause(String playerName) {
        if (state != GameState.RUNNING) return;
        state = GameState.PAUSED;
        pausedBy = playerName;
        findPlayer(playerName).ifPresent(p -> p.setReady(false));
    }

    public synchronized void resume() {
        if (state != GameState.PAUSED) return;
        state = GameState.RUNNING;
        pausedBy = null;
    }

    public synchronized void stop() {
        if (state == GameState.WAITING) return;
        state = GameState.WAITING;
        arrows.clear();
        targets.clear();
        players.forEach(p -> p.setReady(false));
        pausedBy = null;
        winnerName = null;
    }

    public synchronized void shoot(String playerName) {
        if (state != GameState.RUNNING) return;
        boolean alreadyShot = arrows.stream()
                .anyMatch(a -> a.isActive() && a.getOwner().getName().equals(playerName));
        if (alreadyShot) return;

        Optional<Player> op = findPlayer(playerName);
        if (op.isEmpty()) return;
        Player player = op.get();
        player.addShot();
        int index = players.indexOf(player);
        if (index >= 0 && index < PLAYER_START_Y.length) {
            Arrow arrow = new Arrow(player, ARROW_START_X, PLAYER_START_Y[index], ARROW_SPEED);
            arrows.add(arrow);
        }
    }

    public synchronized void update() {
        if (state != GameState.RUNNING) return;

        targets.forEach(t -> t.move(HEIGHT));

        Iterator<Arrow> it = arrows.iterator();
        while (it.hasNext()) {
            Arrow a = it.next();
            if (!a.isActive()) { it.remove(); continue; }
            a.move(WIDTH);
            if (!a.isActive()) { it.remove(); continue; }

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

        for (Player p : players) {
            if (p.getScore() >= WIN_SCORE) {
                winnerName = p.getName();
                state = GameState.WAITING;
                arrows.clear();
                players.forEach(pl -> pl.setReady(false));
                break;
            }
        }
    }

    public synchronized GameStateDTO getCurrentState() {
        List<TargetDTO> tDTOs = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            Target t = targets.get(i);
            tDTOs.add(new TargetDTO(i, t.getX(), t.getY(), t.getRadius(), true));
        }

        List<ArrowDTO> aDTOs = new ArrayList<>();
        for (Arrow a : arrows) {
            if (!a.isActive()) continue;
            int ownerId = players.indexOf(a.getOwner());
            if (ownerId >= 0) {
                aDTOs.add(new ArrowDTO(a.getX(), a.getY(), ownerId, true));
            }
        }

        List<PlayerDTO> pDTOs = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            pDTOs.add(new PlayerDTO(i, p.getName(), p.getScore(), p.getShots(), p.isReady()));
        }
        return new GameStateDTO(tDTOs, aDTOs, pDTOs, state, pausedBy, winnerName);
    }

    public synchronized List<Player> getPlayers() { return new ArrayList<>(players); }
    public synchronized GameState getState() { return state; }

    private Optional<Player> findPlayer(String name) {
        return players.stream().filter(p -> p.getName().equals(name)).findFirst();
    }
}
