package org.example.marksmangame.server;

import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.GameStateDTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 4;

    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final Engine engine = new Engine();
    private GameLoop gameLoop;


    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            gameLoop = new GameLoop(engine, this);
            gameLoop.start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                synchronized (clients) {
                    if (clients.size() >= MAX_PLAYERS) { clientSocket.close(); continue; }
                    ClientHandler handler = new ClientHandler(clientSocket, this);
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
                    if (client.getPlayer() != null) {
                        engine.removePlayer(client.getPlayer());
                    }
                    it.remove();
                }
            }

            if (engine.getPlayers().isEmpty() && engine.getState() != GameState.WAITING) {
                engine.stop();
                System.out.println("All players disconnected, game stopped. Server waiting for new players.");
            }
        }
    }

    public void notifyStateChange() {
        broadcast(engine.getCurrentState());
    }

    public void registerClient(ClientHandler handler) {
        synchronized (clients) { clients.add(handler); }
    }

    public void removeClient(ClientHandler client) {
        synchronized (clients) { clients.remove(client); }
    }

    public Engine getEngine() { return engine; }
    public GameLoop getGameLoop() { return gameLoop; }
}
