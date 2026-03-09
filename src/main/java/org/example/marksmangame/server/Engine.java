package org.example.marksmangame.server;

import org.example.marksmangame.dto.*;
import org.example.marksmangame.model.*;

import java.util.*;

public class Engine {
    public static final int MAX_PLAYERS = 4;
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


    public Player addPlayer(String name) {
        if (state != GameState.WAITING) return null;
        if (players.size() >= MAX_PLAYERS) return null;
        if (players.stream().anyMatch(p -> p.getName().equals(name))) return null;

        Player p = new Player(name);
        players.add(p);
        return p;
    }

    public void removePlayerByName(String name) {
        Optional<Player> op = players.stream().filter(p -> p.getName().equals(name)).findFirst();
        if (op.isEmpty()) return;
        Player p = op.get();

        arrows.removeIf(a -> a.getOwner().getName().equals(name));
        players.remove(p);

        if (Objects.equals(pausedBy, name)) {
            pausedBy = null;
            if (state == GameState.PAUSED) state = GameState.WAITING;
        }

        if (players.isEmpty()) {
            stop();
        }
    }

    public void setPlayerReady(String playerName, boolean ready) {
        findPlayer(playerName).ifPresent(p -> p.setReady(ready));
    }

    public boolean allPlayersReady() {
        return !players.isEmpty() && players.stream().allMatch(Player::isReady);
    }

    public void start() {
        if (state == GameState.RUNNING) return;
        players.forEach(Player::reset);
        arrows.clear();
        targets.clear();
        createDefaultTargets();
        state = GameState.RUNNING;
        winnerName = null;
        pausedBy = null;
    }

    private void createDefaultTargets() {
        targets.clear();
        targets.add(TargetType.NEAR.create(700, 300));
        targets.add(TargetType.FAR.create(850, 300));
    }

    public void pause(String playerName) {
        if (state != GameState.RUNNING) return;
        state = GameState.PAUSED;
        pausedBy = playerName;
        findPlayer(playerName).ifPresent(p -> p.setReady(false));
    }

    public void resume() {
        if (state != GameState.PAUSED) return;
        state = GameState.RUNNING;
        pausedBy = null;
    }

    public void stop() {
        if (state == GameState.WAITING) return;
        state = GameState.WAITING;
        arrows.clear();
        targets.clear();
        players.forEach(p -> p.setReady(false));
        pausedBy = null;
        winnerName = null;
    }

    public void shoot(String playerName) {
        if (state != GameState.RUNNING) return;
        Optional<Player> op = findPlayer(playerName);
        if (op.isEmpty()) return;
        Player player = op.get();

        boolean alreadyShot = arrows.stream()
                .anyMatch(a -> a.isActive() && a.getOwner().getName().equals(playerName));
        if (alreadyShot) return;

        int index = players.indexOf(player);
        if (index >= 0 && index < PLAYER_START_Y.length) {
            Arrow arrow = new Arrow(player, ARROW_START_X, PLAYER_START_Y[index], ARROW_SPEED);
            arrows.add(arrow);
            player.addShot();
        }
    }

    public void update() {
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

    public GameStateDTO getCurrentState() {
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

    public  GameState getState() { return state; }

    private Optional<Player> findPlayer(String name) {
        return players.stream().filter(p -> p.getName().equals(name)).findFirst();
    }
}
