package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.network.ClientHandler;
import org.example.marksmangame.network.GameContext;
import org.example.marksmangame.network.MessageType;

public class HistoryCommand implements org.example.marksmangame.network.command.Command {
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
