package org.example.marksmangame.server;

import org.example.marksmangame.dto.GameStateDTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameServer {
    private ServerSocket serverSocket;
    private final int port = 12345;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final Engine engine = new Engine();
    private GameLoop gameLoop;
    private final int MAX_PLAYERS = 4;

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            gameLoop = new GameLoop(engine, this);
            gameLoop.start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                synchronized (clients) {
                    if (clients.size() >= MAX_PLAYERS) {
                        clientSocket.close();
                        continue;
                    }
                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    clients.add(handler);
                    handler.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(GameStateDTO state) {
        synchronized (clients) {
            Iterator<ClientHandler> it = clients.iterator();
            while (it.hasNext()) {
                ClientHandler client = it.next();
                if (!client.sendState(state)) {
                    it.remove();
                }
            }
        }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

    public Engine getEngine() { return engine; }
    public GameLoop getGameLoop() { return gameLoop; }
}
