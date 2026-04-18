package org.example.marksmangame.client;

import javafx.application.Platform;
import org.example.marksmangame.dto.*;
import org.example.marksmangame.server.network.Connection;
import org.example.marksmangame.server.network.Message;
import org.example.marksmangame.server.network.MessageType;

import java.io.IOException;
import java.net.Socket;

public class GameClient {
    private final Connection connection;
    private final String playerName;
    private final GameClientView view;
    private volatile GameStateDTO lastState;

    public GameClient(String serverAddress, int port, String playerName, GameClientView view) throws IOException {
        this.playerName = playerName;
        this.view = view;
        Socket socket = new Socket(serverAddress, port);
        connection = new Connection(socket);
    }

    public void start() {
        new Thread(() -> {
            try {
                label:
                while (true) {
                    Message<?> msg = connection.read();
                    if (msg == null) {
                        Platform.runLater(() ->
                                view.connectionRefused("Connection closed"));
                        break;
                    }
                    switch (msg.type()) {

                        case GAME_STATE -> {
                            lastState = (GameStateDTO) msg.payload();
                        }

                        case LEADERBOARD -> {
                            LeaderboardDTO dto = (LeaderboardDTO) msg.payload();
                            Platform.runLater(() -> view.showLeaderboard(dto));
                        }

                        case HISTORY -> {
                            GameHistoryDTO dto = (GameHistoryDTO) msg.payload();
                            Platform.runLater(() -> view.showHistory(dto));
                        }

                        case ERROR -> {
                            ErrorDTO err = (ErrorDTO) msg.payload();
                            Platform.runLater(() ->
                                    view.connectionRefused(err.message()));
                        }
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() ->
                        view.connectionRefused("Connection lost")
                );
            } finally {
                connection.close();
            }
        }).start();
    }

    public void sendCommand(CommandDTO command) {
        connection.send(MessageType.COMMAND, command);
    }

    public void disconnect() {
        sendCommand(new CommandDTO(CommandType.DISCONNECT, playerName));
        if (!connection.isClosed()) connection.close();
    }

    private <T> T convert(Object payload, Class<T> clazz) {
        return connection.getGson().fromJson(
                connection.getGson().toJson(payload), clazz
        );
    }

    public GameStateDTO getLastState() {
        return lastState;
    }
}
