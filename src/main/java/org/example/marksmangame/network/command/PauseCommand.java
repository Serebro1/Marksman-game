package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.network.ClientHandler;
import org.example.marksmangame.network.GameContext;

public class PauseCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        context.engine().pause(cmd.playerName());
    }
}
