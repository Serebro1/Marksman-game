package org.example.marksmangame.server.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.server.network.ClientHandler;
import org.example.marksmangame.server.network.GameContext;

public class DisconnectCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        context.engine().removePlayerByName(cmd.playerName());
        context.server().removeClient(client);
    }
}
