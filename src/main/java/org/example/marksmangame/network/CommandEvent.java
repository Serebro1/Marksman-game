package org.example.marksmangame.network;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.server.ClientHandler;

public class CommandEvent {

    public final ClientHandler client;
    public final CommandDTO command;

    public CommandEvent(ClientHandler client, CommandDTO command) {
        this.client = client;
        this.command = command;
    }
}
