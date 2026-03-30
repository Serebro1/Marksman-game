package org.example.marksmangame.server.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.server.ClientHandler;
import org.example.marksmangame.server.GameContext;

@FunctionalInterface
public interface Command {
    void execute(ClientHandler client, CommandDTO cmd, GameContext context);
}
