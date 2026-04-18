package org.example.marksmangame.network;

import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.network.command.Command;
import org.example.marksmangame.network.command.CommandRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameLoop implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GameLoop.class);

    private final CommandRegistry commandRegistry;
    private final GameContext context;
    private final boolean running = true;

    public GameLoop(GameContext context) {
        this.commandRegistry = new CommandRegistry();
        this.context = context;
    }

    @Override
    public void run() {
        final int TICK_MS = 16;

        while (running) {
            processCommands();

            if (context.engine().getState() == GameState.RUNNING) {
                context.engine().update();
                String winner = context.engine().consumeWinnerName();
                if (winner != null) {
                    context.service().recordWin(winner);
                    context.service().finishGame(context.engine(), winner);
                }
            }

            context.server().broadcast(context.engine().getCurrentState());

            try {
                Thread.sleep(TICK_MS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void processCommands() {
        CommandEvent ev;
        while ((ev = context.server().pollCommand()) != null) {
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
