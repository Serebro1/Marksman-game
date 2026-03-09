package org.example.marksmangame;

import org.example.marksmangame.server.GameServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        new GameServer().start();
    }
}
