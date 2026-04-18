package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.server.game.model.Player;
import org.example.marksmangame.server.ClientHandler;
import org.example.marksmangame.server.GameContext;

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
