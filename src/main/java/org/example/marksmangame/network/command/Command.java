package org.example.marksmangame.network.command;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.network.ClientHandler;
import org.example.marksmangame.network.GameContext;

@FunctionalInterface
public interface Command {
    void execute(ClientHandler client, CommandDTO cmd, GameContext context);
}
