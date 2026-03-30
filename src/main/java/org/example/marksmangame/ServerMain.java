package org.example.marksmangame;

import org.example.marksmangame.server.network.GameServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        new GameServer().start();
    }
}
