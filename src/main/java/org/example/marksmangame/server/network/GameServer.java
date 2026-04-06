package org.example.marksmangame.server.network;

import org.example.marksmangame.server.db.GameService;
import org.example.marksmangame.dto.GameStateDTO;
import org.example.marksmangame.server.game.Engine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServer {
    private static final int PORT = 12345;

    private final GameContext context = new GameContext(new Engine(), new GameService(), this);

    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    private final BlockingQueue<CommandEvent> commandQueue = new LinkedBlockingQueue<>();

    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        GameLoop loop = new GameLoop(context);
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
            boolean ok = client.send(state);
            if (!ok) {
                client.closeSilently();
                removeClient(client);
                String name = client.getPlayerName();
                if (name != null) {
                    context.engine().removePlayerByName(name);
                }
            }
        }
    }
}
