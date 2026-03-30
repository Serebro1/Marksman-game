package org.example.marksmangame.server.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.server.ClientHandler;
import org.example.marksmangame.server.GameContext;

public class HistoryCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        if (context.engine().getState() == GameState.RUNNING) {
            context.engine().pause(cmd.playerName());
        }
        client.send(context.service().getHistory(10));
    }
}
