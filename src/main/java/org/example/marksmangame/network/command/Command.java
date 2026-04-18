package org.example.marksmangame.server.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.server.network.ClientHandler;
import org.example.marksmangame.server.network.GameContext;

@FunctionalInterface
public interface Command {
    void execute(ClientHandler client, CommandDTO cmd, GameContext context);
}
