package org.example.marksmangame.server.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.ErrorDTO;
import org.example.marksmangame.server.game.model.Player;
import org.example.marksmangame.server.network.ClientHandler;
import org.example.marksmangame.server.network.GameContext;
import org.example.marksmangame.server.network.MessageType;

public class ConnectCommand implements Command {
    @Override
    public void execute(ClientHandler client, CommandDTO cmd, GameContext context) {
        Player p = context.engine().addPlayer(cmd.playerName());
        if (p == null) {
            context.server().sendError(client, "Name already taken");
            return;
        }

        client.setPlayerName(p.getName());
        context.service().registerPlayer(p.getName());
    }
}
