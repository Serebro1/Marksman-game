package org.example.marksmangame.client;

import javafx.application.Platform;
import org.example.marksmangame.dto.*;
import org.example.marksmangame.server.Connection;

import java.io.IOException;
import java.net.Socket;

public class GameClient {
    private final Connection connect;
    private final String playerName;
    private final GameClientView view;
    private volatile GameStateDTO lastState;

    public GameClient(String serverAddress, int port, String playerName, GameClientView view) throws IOException {
        this.playerName = playerName;
        this.view = view;
        Socket socket = new Socket(serverAddress, port);
        connect = new Connection(socket);
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Object obj = connect.read();
                    if (obj instanceof GameStateDTO state) {
                        lastState = state;
                    } else if (obj instanceof LeaderboardDTO leaderboard) {
                        Platform.runLater(() -> view.showLeaderboard(leaderboard));
                    } else if (obj instanceof GameHistoryDTO history) {
                        Platform.runLater(() -> view.showHistory(history));
                    }else if (obj == null) {
                        Platform.runLater(() -> view.connectionRefused("Name already taken or server not waiting"));
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                Platform.runLater(() ->
                        view.connectionRefused("Connection lost")
                );
            } finally {
                connect.close();
            }
        }).start();
    }

    public void sendCommand(CommandDTO command) {
        try {
            connect.send(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        sendCommand(new CommandDTO(CommandType.DISCONNECT, playerName));
        if (connect != null && !connect.isClosed()) connect.close();
    }

    public GameStateDTO getLastState() {
        return lastState;
    }
}
