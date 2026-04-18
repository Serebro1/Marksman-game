package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.server.ClientHandler;
import org.example.marksmangame.server.GameContext;
import org.example.marksmangame.network.MessageType;

public class LeaderboardCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        if (context.engine().getState() == GameState.RUNNING) {
            context.engine().pause(cmd.playerName());
        }
        client.send(
                MessageType.LEADERBOARD,
                context.service().getLeaderboard(10)
        );
    }
}
