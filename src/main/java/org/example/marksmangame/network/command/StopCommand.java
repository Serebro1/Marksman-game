package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.network.ClientHandler;
import org.example.marksmangame.network.GameContext;

public class StopCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        context.engine().stop();
    }
}
