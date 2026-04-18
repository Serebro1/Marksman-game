package org.example.marksmangame.server.network;

import com.google.gson.Gson;
import org.example.marksmangame.dto.*;

import java.io.IOException;

public class ClientHandler implements Runnable {
    private final Connection connection;
    private final GameServer server;

    private volatile String playerName;
    private volatile boolean closed = false;

    public ClientHandler(Connection connect, GameServer server) {
        this.connection = connect;
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
            server.registerClient(this);

            while (!connection.isClosed()) {
                Message<?> msg = connection.read();
                if (msg == null) break;
                if (msg.type() != MessageType.COMMAND) {
                    server.sendError(this, "Invalid message type");
                    continue;
                }

                CommandDTO cmd = connection.getGson().fromJson(
                        connection.getGson().toJson(msg.payload()),
                        CommandDTO.class
                );
                server.enqueueCommand(new CommandEvent(this, cmd));

            }

        } catch (IOException ex) {
            handleDisconnect();
        } finally {
            closeSilently();
        }
    }

    private void handleDisconnect() {
        if (playerName != null) {
            server.enqueueCommand(
                    new CommandEvent(
                            this,
                            new CommandDTO(CommandType.DISCONNECT, playerName)
                    )
            );
        }
    }
    public void send(MessageType type, Object payload) {
        connection.send(type, payload);
    }

    public void sendError(String message) {
        connection.send(MessageType.ERROR,
                new ErrorDTO(message, 500));
    }

    public void closeSilently() {
        if (closed) return;
        closed = true;
        connection.close();
        server.removeClient(this);
    }

    public void cleanupForcibly() {
        closeSilently();
    }
}
