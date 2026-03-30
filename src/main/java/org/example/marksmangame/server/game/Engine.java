package org.example.marksmangame.server.game;

import org.example.marksmangame.dto.*;
import org.example.marksmangame.server.game.model.*;

import java.util.*;

public class Engine {
    public static final int MAX_PLAYERS = 4;
    private static final double HEIGHT = 600;
    private static final double WIDTH  = 850;
    private static final double ARROW_SPEED = 8;
    private static final double ARROW_START_X = 150;
    private static final double[] PLAYER_START_Y = {200, 300, 400, 500};
    private static final int WIN_SCORE = 6;

    private final PlayerManager playerManager = new PlayerManager();
    private final GameStateController stateController = new GameStateController();
    private final GameMechanics mechanics = new GameMechanics(
            HEIGHT, WIDTH, ARROW_SPEED, ARROW_START_X, PLAYER_START_Y, WIN_SCORE);


    public Player addPlayer(String name) {
        if (stateController.getState() != GameState.WAITING) return null;
        if (playerManager.size() >= MAX_PLAYERS) return null;
        if (playerManager.findPlayer(name).isPresent()) return null;

        Player p = new Player(name);
        if (playerManager.addPlayer(p)) {
            return p;
        }
        return null;
    }

    public void removePlayerByName(String name) {
        Optional<Player> op = playerManager.findPlayer(name);
        if (op.isEmpty()) return;
        Player p = op.get();
        mechanics.getArrows().removeIf(a -> a.getOwner().equals(p));
        playerManager.removePlayer(p);

        if (stateController.getPausedBy() != null && stateController.getPausedBy().equals(name)) {
            stateController.removePlayer();
        }

        if (playerManager.isEmpty()) {
            stop();
        }
    }

    public void setPlayerReady(String playerName, boolean ready) {
        playerManager.setPlayerReady(playerName, ready);
    }

    public boolean allPlayersReady() {
        return playerManager.allPlayersReady();
    }

    public void start() {
        stateController.start();
        for (Player p : playerManager.getPlayers()) {
            p.reset();
        }
        mechanics.clearArrowsAndTargets();
        mechanics.createDefaultTargets();

    }

    public void pause(String playerName) {
        stateController.pause(playerName);
        playerManager.setPlayerReady(playerName, false);
    }

    public void resume() {
        stateController.resume();
    }

    public void stop() {
        stateController.stop();
        mechanics.clearArrowsAndTargets();
        for (Player p : playerManager.getPlayers()) {
            p.setReady(false);
        }
    }

    public void shoot(String playerName) {
        if (stateController.getState() != GameState.RUNNING) return;
        Optional<Player> op = playerManager.findPlayer(playerName);
        if (op.isEmpty()) return;
        Player player = op.get();

        int index = playerManager.getPlayers().indexOf(player);
        mechanics.shoot(player, index);
    }

    public void update() {
        mechanics.update(playerManager, stateController);
    }

    public GameStateDTO getCurrentState() {
        return GameStateDTOFactory.create(playerManager, mechanics, stateController);
    }

    public  GameState getState() { return stateController.getState(); }
    public List<Player> getPlayers() {
        return playerManager.getPlayers();
    }

    public String consumeWinnerName() {
        return stateController.consumeWinnerName();
    }
}
