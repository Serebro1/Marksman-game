package org.example.marksmangame.server;

import org.example.marksmangame.db.GameService;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.server.command.Command;
import org.example.marksmangame.server.command.CommandRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLoop implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GameLoop.class);

    private final GameServer server;
    private final Engine engine;
    private final GameService service;
    private final CommandRegistry commandRegistry;
    private final GameContext context;
    private final boolean running = true;

    public GameLoop(GameServer server, Engine engine, GameService service) {
        this.server = server;
        this.engine = engine;
        this.service = service;
        this.commandRegistry = new CommandRegistry();
        this.context = new GameContext(engine, service, server);
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

            server.broadcast(engine.getCurrentState());

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
            Command command = commandRegistry.getCommand(cmd.type());
            if (command != null) {
                command.execute(client, cmd, context);
            } else {
                log.warn("Unknown command type: {} from client: {}", cmd.type(), client);
            }
        }
    }
}
