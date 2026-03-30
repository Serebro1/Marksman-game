package org.example.marksmangame.server.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.server.ClientHandler;
import org.example.marksmangame.server.GameContext;

public class PauseCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        context.engine().pause(cmd.playerName());
    }
}
