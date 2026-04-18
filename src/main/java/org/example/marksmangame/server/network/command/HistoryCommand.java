package org.example.marksmangame.server.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.server.network.ClientHandler;
import org.example.marksmangame.server.network.GameContext;
import org.example.marksmangame.server.network.MessageType;

public class HistoryCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        if (context.engine().getState() == GameState.RUNNING) {
            context.engine().pause(cmd.playerName());
        }

        client.send(
                MessageType.HISTORY,
                context.service().getHistory(10)
        );
    }
}
