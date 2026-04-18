package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.network.ClientHandler;
import org.example.marksmangame.network.GameContext;

public class DisconnectCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        context.engine().removePlayerByName(cmd.playerName());
        context.server().removeClient(client);
    }
}
