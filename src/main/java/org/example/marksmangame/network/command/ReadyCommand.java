package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.server.ClientHandler;
import org.example.marksmangame.server.GameContext;


public class ReadyCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        context.engine().setPlayerReady(cmd.playerName(), true);

        if (!context.engine().allPlayersReady()) return;

        if (context.engine().getState() == GameState.WAITING || context.engine().getState() == GameState.FINISHED) {
            context.engine().start();
            context.service().startGame(generateGameName());
        } else if (context.engine().getState() == GameState.PAUSED) {
            context.engine().resume();
        }
    }

    public static String generateGameName() {
        return "Game_" + System.currentTimeMillis();
    }
}
