package org.example.marksmangame.server;

import org.example.marksmangame.dto.GameStateDTO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServer {
    private static final int PORT = 12345;

    private final Engine engine = new Engine();

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    private final BlockingQueue<CommandEvent> commandQueue = new LinkedBlockingQueue<>();

    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        GameLoop loop = new GameLoop(engine, this);
        new Thread(loop, "GameLoop").start();

        while (running) {
            Socket s = serverSocket.accept();
            Connection connect = new Connection(s);
            ClientHandler handler = new ClientHandler(connect, this);
            new Thread(handler, "ClientHandler-" + s.getRemoteSocketAddress()).start();
        }
    }

    public void enqueueCommand(CommandEvent ev) {
        commandQueue.offer(ev);
    }

    public CommandEvent pollCommand() {
        return commandQueue.poll();
    }

    public void registerClient(ClientHandler client) {
        clients.add(client);
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public void broadcast(GameStateDTO state) {
        for (ClientHandler client : clients) {
            boolean ok = client.sendState(state);
            if (!ok) {
                client.closeSilently();
                removeClient(client);
                String name = client.getPlayerName();
                if (name != null) {
                    engine.removePlayerByName(name);
                }
            }
        }
    }
}
