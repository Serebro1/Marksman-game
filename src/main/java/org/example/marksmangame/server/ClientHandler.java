package org.example.marksmangame.server;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.CommandType;
import org.example.marksmangame.dto.GameState;
import org.example.marksmangame.dto.GameStateDTO;
import org.example.marksmangame.model.Player;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameServer server;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private volatile String playerName;
    private volatile boolean closed = false;


    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            server.registerClient(this);

            while (!socket.isClosed()) {
                Object obj = in.readObject();
                if (!(obj instanceof CommandDTO cmd)) continue;
                server.enqueueCommand(new CommandEvent(this, cmd));
            }

        } catch (EOFException eof) {
            if (playerName != null) {
                server.enqueueCommand(new CommandEvent(
                        this, new CommandDTO(CommandType.DISCONNECT, playerName))
                );
            }
        } catch (IOException | ClassNotFoundException ex) {
            if (playerName != null) {
                server.enqueueCommand(new CommandEvent(
                        this, new CommandDTO(CommandType.DISCONNECT, playerName))
                );
            }
        } finally {
            closeSilently();
        }
    }

    public boolean sendState(GameStateDTO state) {
        try {
            if (out == null || socket == null || socket.isClosed()) return false;
            synchronized (out) {
                out.writeObject(state);
                out.flush();
                out.reset();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void closeSilently() {
        if (closed) return;
        closed = true;
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
    }

    public void cleanupForcibly() {
        closeSilently();
    }
}
