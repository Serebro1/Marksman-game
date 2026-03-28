package org.example.marksmangame.server;

import org.example.marksmangame.db.GameService;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.GameStateDTO;
import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.model.Player;

public class GameLoop implements Runnable {
    private final GameServer server;
    private final Engine engine;
    private final GameService service;
    private volatile boolean running = true;

    public GameLoop(GameServer server, Engine engine, GameService service) {
        this.server = server;
        this.engine = engine;
        this.service = service;
    }

    @Override
    public void run() {
        final int TICK_MS = 16;

        while (running) {
            processCommands();

            if (engine.getState() == GameState.RUNNING) {
                engine.update();
                String winner = engine.consumeWinnerName();
                if (winner != null) {
                    service.recordWin(winner);
                    service.finishGame(engine, winner);
                }
            }

            GameStateDTO state = engine.getCurrentState();
            server.broadcast(state);

            try {
                Thread.sleep(TICK_MS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processCommands() {
        CommandEvent ev;
        while ((ev = server.pollCommand()) != null) {
            ClientHandler client = ev.client;
            CommandDTO cmd = ev.command;

            switch (cmd.type()) {
                case CONNECT -> {
                    Player p = engine.addPlayer(cmd.playerName());
                    if (p == null) {
                        client.sendState(null);
                    } else {
                        client.setPlayerName(p.getName());
                        service.registerPlayer(p.getName());
                        server.broadcast(engine.getCurrentState());
                    }
                }
                case READY -> {
                    engine.setPlayerReady(cmd.playerName(), true);
                    if (engine.allPlayersReady() && (engine.getState() == GameState.WAITING || engine.getState() == GameState.FINISHED)) {
                        engine.start();
                        service.startGame(generateGameName());
                    } else if (engine.getState() == GameState.PAUSED && engine.allPlayersReady()) {
                        engine.resume();
                    }
                    server.broadcast(engine.getCurrentState());
                }
                case PAUSE -> {
                    engine.pause(cmd.playerName());
                    server.broadcast(engine.getCurrentState());
                }
                case RESUME -> {
                    if (engine.allPlayersReady()) {
                        engine.resume();
                    }
                    server.broadcast(engine.getCurrentState());
                }
                case SHOOT -> engine.shoot(cmd.playerName());
                case STOP -> {
                    engine.stop();
                    server.broadcast(engine.getCurrentState());
                }
                case DISCONNECT -> {
                    engine.removePlayerByName(cmd.playerName());
                    server.removeClient(client);
                    server.broadcast(engine.getCurrentState());
                }
                case LEADERBOARD -> {
                    if (engine.getState() == GameState.RUNNING) {
                        engine.pause(cmd.playerName());
                    }
                    client.sendLeaderboard(service.getLeaderboard(10));
                    server.broadcast(engine.getCurrentState());
                }
                case HISTORY -> {
                    if (engine.getState() == GameState.RUNNING) {
                        engine.pause(cmd.playerName());
                    }

                    client.sendHistory(service.getHistory(10));

                    server.broadcast(engine.getCurrentState());
                }
            }
        }
    }

    private String generateGameName() {
        return "Game_" + System.currentTimeMillis();
    }
}
