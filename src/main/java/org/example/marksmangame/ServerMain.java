package org.example.marksmangame;

import org.example.marksmangame.server.GameServer;

public class ServerMain {
    public static void main(String[] args) {
        new GameServer().start();
    }
}
