package org.example.marksmangame.client;

import javafx.application.Platform;
import org.example.marksmangame.dto.CommandDTO;
import org.example.marksmangame.dto.CommandType;
import org.example.marksmangame.dto.GameStateDTO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final String playerName;
    private final GameClientView view;
    private volatile GameStateDTO lastState;

    public GameClient(String serverAddress, int port, String playerName, GameClientView view) throws IOException {
        this.playerName = playerName;
        this.view = view;
        socket = new Socket(serverAddress, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Object obj = in.readObject();
                    if (obj instanceof GameStateDTO) {
                        lastState = (GameStateDTO) obj;
                    } else if (obj == null) {
                        Platform.runLater(() -> view.connectionRefused("Name already taken or server not waiting"));
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try { socket.close(); } catch (IOException e) {}
            }
        }).start();
    }

    public void sendCommand(CommandDTO command) {
        try {
            out.writeObject(command);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        sendCommand(new CommandDTO(CommandType.DISCONNECT, playerName));
        try { socket.close(); } catch (IOException e) {}
    }

    public GameStateDTO getLastState() {
        return lastState;
    }
}
