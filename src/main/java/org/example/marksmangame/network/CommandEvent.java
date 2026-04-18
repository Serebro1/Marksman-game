package org.example.marksmangame.server.network;

import org.example.marksmangame.dto.CommandDTO;

public class CommandEvent {

    public final ClientHandler client;
    public final CommandDTO command;

    public CommandEvent(ClientHandler client, CommandDTO command) {
        this.client = client;
        this.command = command;
    }
}
