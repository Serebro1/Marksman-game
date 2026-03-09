package org.example.marksmangame.server;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.GameStateDTO;
import org.example.marksmangame.model.Player;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final GameServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Player player;

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                CommandDTO cmd = (CommandDTO) in.readObject();
                handleCommand(cmd);
            }

        } catch (EOFException e) {
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private void handleCommand(CommandDTO cmd) throws IOException {
        Engine engine = server.getEngine();
        switch (cmd.type()) {
            case CONNECT -> handleConnect(cmd.playerName(), engine);
            case READY   -> handleReady(engine);
            case PAUSE   -> handlePause(engine);
            case RESUME  -> handleResume(engine);
            case SHOOT   -> handleShoot(engine);
            case STOP    -> handleStop(engine);
            case DISCONNECT -> handleDisconnect();
        }
    }

    private void handleConnect(String name, Engine engine) {
        synchronized (engine) {
            if (engine.getState() != GameState.WAITING) {
                sendState(null);
                return;
            }

            boolean nameTaken = engine.getPlayers().stream().anyMatch(p -> p.getName().equals(name));
            if (nameTaken) {
                sendState(null);
                return;
            }

            player = new Player(name);
            engine.addPlayer(player);
        }
        sendState(server.getEngine().getCurrentState());
        server.registerClient(this);
        server.notifyStateChange();
    }

    private void handleReady(Engine engine) {
        if (player == null) return;
        engine.setPlayerReady(player.getName(), true);

        if (engine.allPlayersReady()) {
            if (engine.getState() == GameState.WAITING || engine.getState() == GameState.FINISHED) {
                engine.start();
            } else if (engine.getState() == GameState.PAUSED) {
                engine.resume();
                server.getGameLoop().resumeLoop();
            }
        }
        server.notifyStateChange();
    }

    private void handlePause(Engine engine) {
        if (player == null) return;
        engine.pause(player.getName());
        server.getGameLoop().pauseLoop();
        server.notifyStateChange();
    }

    private void handleResume(Engine engine) {
        if (player == null) return;
        if (engine.getState() == GameState.PAUSED && engine.allPlayersReady()) {
            engine.resume();
            server.getGameLoop().resumeLoop();
            server.notifyStateChange();
        }
    }

    private void handleShoot(Engine engine) {
        if (player == null) return;
        engine.shoot(player.getName());
    }

    private void handleStop(Engine engine) {
        if (player == null) return;
        engine.stop();
        server.getGameLoop().resumeLoop();
        server.notifyStateChange();
    }

    private void handleDisconnect() {
        cleanup();
        server.notifyStateChange();
    }

    public boolean sendState(GameStateDTO state) {
        try {
            if (socket != null && !socket.isClosed() && out != null) {
                out.writeObject(state);
                out.flush();
                out.reset();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Failed to send state to " + (player != null ? player.getName() : "unknown"));
        }
        return false;
    }

    private void cleanup() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
        if (player != null) { server.getEngine().removePlayer(player); }
        server.removeClient(this);
    }
}
