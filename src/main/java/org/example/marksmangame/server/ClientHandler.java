package org.example.marksmangame.server;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.GameStateDTO;
import org.example.marksmangame.model.Player;

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
                CommandDTO command = (CommandDTO) in.readObject();
                Engine engine = server.getEngine();

                switch (command.getType()) {
                    case CONNECT:
                        String name = command.getPlayerName();
                        synchronized (engine) {
                            if (engine.getState() != GameState.WAITING ||
                                    engine.getPlayers().stream().anyMatch(p -> p.getName().equals(name))) {
                                out.writeObject(null);
                                out.flush();
                                return;
                            }
                            player = new Player(name);
                            engine.addPlayer(player);
                        }
                        out.writeObject(engine.getCurrentState());
                        out.flush();
                        break;

                    case READY:
                        if (player == null) break;
                        if (engine.getState() == GameState.PAUSED) {
                            engine.resume();
                            server.getGameLoop().resumeLoop();
                        } else {
                            engine.setPlayerReady(player.getName(), true);
                        }
                        break;

                    case PAUSE:
                        if (player != null) {
                            engine.pause();
                            server.getGameLoop().pauseLoop();
                        }
                        break;
                    case RESUME:
                        if (player != null && engine.getState() == GameState.PAUSED) {
                            engine.resume();
                            server.getGameLoop().resumeLoop();
                        }
                        break;
                    case SHOOT:
                        if (player != null) {
                            engine.shoot(player.getName());
                        }
                        break;
                    case STOP:
                        if (player != null) {
                            engine.stop();
                            server.getGameLoop().resumeLoop();
                        }
                        break;
                    case DISCONNECT:
                        return;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException e) {}
            if (player != null) {
                server.getEngine().removePlayer(player);
            }
            server.removeClient(this);
        }
    }

    public boolean sendState(GameStateDTO state) {
        try {
            out.writeObject(state);
            out.flush();
            out.reset();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
