package org.example.marksmangame.server;

import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.CommandType;
import org.example.marksmangame.dto.GameStateDTO;
import org.example.marksmangame.dto.LeaderboardDTO;

import java.io.EOFException;
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
                Object obj = connection.read();
                if (!(obj instanceof CommandDTO cmd)) continue;
                server.enqueueCommand(new CommandEvent(this, cmd));
            }

        } catch (EOFException eof) {
            handleDisconnect();
        } catch (IOException | ClassNotFoundException ex) {
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

    public boolean sendState(GameStateDTO state) {
        try {
            connection.send(state);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean sendLeaderboard(LeaderboardDTO leaderboard) {
        try {
            connection.send(leaderboard);
            return true;
        } catch (IOException e) {
            return false;
        }
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
