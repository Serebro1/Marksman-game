package org.example.marksmangame.server;

import org.example.marksmangame.dto.*;
import org.example.marksmangame.model.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Engine {
    private final List<Player> players = new ArrayList<>();
    private final List<Target> targets = new ArrayList<>();
    private final List<Arrow> arrows = new ArrayList<>();
    private GameState state = GameState.WAITING;
    private String winnerName = null;

    private static final double HEIGHT = 600;
    private static final double WIDTH = 1000;
    private static final double[] PLAYER_START_Y = {200, 300, 400, 500};

    public synchronized void addPlayer(Player p) {
        players.add(p);
    }

    public synchronized void removePlayer(Player p) {
        players.remove(p);
    }

    public synchronized void setPlayerReady(String playerName, boolean ready) {
        for (Player p : players) {
            if (p.getName().equals(playerName)) {
                p.setReady(ready);
                break;
            }
        }

        if ((state == GameState.WAITING || state == GameState.FINISHED) && allPlayersReady()) {
            start();
        }
    }

    public synchronized boolean allPlayersReady() {
        if (players.isEmpty()) return false;
        for (Player p : players) {
            if (!p.isReady()) return false;
        }
        return true;
    }

    public synchronized void start() {
        if (state == GameState.RUNNING) return;
        for (Player p : players) {
            p.reset();
        }
        arrows.clear();
        targets.clear();
        targets.add(new Target(TargetType.NEAR, 700, 300));
        targets.add(new Target(TargetType.FAR, 850, 300));
        state = GameState.RUNNING;
        winnerName = null;
    }


    public synchronized void pause() {
        if (state == GameState.RUNNING)
            state = GameState.PAUSED;
    }

    public synchronized void resume() {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            for (Player p : players) {
                p.setReady(false);
            }
        }
    }

    public synchronized void stop() {
        if (state == GameState.RUNNING || state == GameState.PAUSED) {
            state = GameState.WAITING;
            arrows.clear();
            targets.clear();
            for (Player p : players) {
                p.setReady(false);
            }
            winnerName = null;
        }
    }

    public synchronized void shoot(String playerName) {
        if (state != GameState.RUNNING) return;
        for (Arrow a : arrows) {
            if (a.isActive() && a.getOwner().getName().equals(playerName)) {
                return;
            }
        }
        Player player = players.stream().filter(p -> p.getName().equals(playerName)).findFirst().orElse(null);
        if (player == null) return;
        player.addShot();
        int index = players.indexOf(player);
        if (index >= 0 && index < PLAYER_START_Y.length) {
            Arrow arrow = new Arrow(player, 150, PLAYER_START_Y[index], 8);
            arrows.add(arrow);
        }
    }

    public void update() {
        if (state != GameState.RUNNING) return;

        targets.forEach(t -> t.move(HEIGHT));

        Iterator<Arrow> it = arrows.iterator();
        while (it.hasNext()) {
            Arrow a = it.next();
            if (!a.isActive()) {
                it.remove();
                continue;
            }
            a.move(WIDTH);
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

        for (Player p : players) {
            if (p.getScore() >= 6) {
                winnerName = p.getName();
                state = GameState.WAITING;
                arrows.clear();

                for (Player pl : players) {
                    pl.setReady(false);
                }
                break;
            }
        }
    }

    public synchronized GameStateDTO getCurrentState() {
        List<TargetDTO> targetDTOs = new ArrayList<>();
        for (int i = 0; i < targets.size(); i++) {
            Target t = targets.get(i);
            targetDTOs.add(new TargetDTO(i, t.getX(), t.getY(), t.getRadius(), true));
        }
        List<ArrowDTO> arrowDTOs = new ArrayList<>();
        for (Arrow a : arrows) {
            int ownerId = players.indexOf(a.getOwner());
            if (a.isActive()) {
                arrowDTOs.add(new ArrowDTO(a.getX(), a.getY(), ownerId, true));
            }
        }
        List<PlayerDTO> playerDTOs = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            playerDTOs.add(new PlayerDTO(i, p.getName(), p.getScore(), p.getShots(), p.isReady()));
        }
        return new GameStateDTO(targetDTOs, arrowDTOs, playerDTOs, state, winnerName);
    }

    public synchronized List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public synchronized GameState getState() {
        return state;
    }
}
