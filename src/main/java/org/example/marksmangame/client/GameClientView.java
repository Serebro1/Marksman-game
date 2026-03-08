package org.example.marksmangame.client;

import javafx.animation.AnimationTimer;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import org.example.marksmangame.client.view.ArrowView;
import org.example.marksmangame.client.view.PlayerView;
import org.example.marksmangame.client.view.TargetView;
import org.example.marksmangame.dto.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameClientView {
    private final BorderPane root = new BorderPane();
    private final Pane gamePane = new Pane();
    private final Pane objectsLayer = new Pane();

    private final double width = 1500;
    private final double height = 600;

    private final ListView<String> playersListView = new ListView<>();
    private final Label statusLabel = new Label("Status: Disconnected");
    private final Button connectButton = new Button("Connect");
    private final Button readyButton = new Button("Ready");
    private final Button pauseButton = new Button("Pause");
    private final Button resumeButton = new Button("Resume");
    private final Button stopButton = new Button("Stop");
    private final Button shootButton = new Button("Shoot");
    private final Button disconnectButton = new Button("Disconnect");
    private final TextField nameField = new TextField("Player");

    private GameClient client;
    private String playerName;
    private AnimationTimer animationTimer;

    private final Map<Integer, TargetView> targetViews = new HashMap<>();
    private final Map<Integer, ArrowView> arrowViews = new HashMap<>();
    private final Map<Integer, PlayerView> playerViews = new HashMap<>();
    private static final Color[] PLAYER_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};
    private static final double[] PLAYER_Y = {200, 300, 400, 500};

    public GameClientView() {
        setupUI();
    }

    private void setupUI() {
        gamePane.setPrefSize(width, height);
        gamePane.getChildren().add(objectsLayer);

        Rectangle zone = new Rectangle(150, height);
        zone.setFill(Color.LIGHTYELLOW);
        zone.setStroke(Color.BLACK);

        Line sep = new Line(150, 0, 150, height);
        sep.setStrokeWidth(3);

        Line guideNear = new Line(700, 0, 700, height);
        guideNear.setStroke(Color.GRAY);
        Line guideFar = new Line(850, 0, 850, height);
        guideFar.setStroke(Color.GRAY);
        gamePane.getChildren().addAll(zone, guideNear, guideFar);

        // панель управления
        HBox controlBox = new HBox(10, readyButton, pauseButton, resumeButton, shootButton, stopButton, disconnectButton);
        controlBox.setLayoutX(150);
        controlBox.setLayoutY(555);
        controlBox.setStyle("-fx-background-color: lightgray; -fx-padding: 10; -fx-border-color: black;");
        gamePane.getChildren().add(controlBox);

        VBox rightPanel = new VBox(10,
                new HBox(10, new Label("Name:"), nameField, connectButton),
                statusLabel,
                new Label("Players:"), playersListView
        );
        rightPanel.setStyle("-fx-padding: 10; -fx-background-color: lightgray;");
        rightPanel.setPrefWidth(300);
        root.setRight(rightPanel);

        root.setCenter(gamePane);

        connectButton.setOnAction(e -> connect());
        readyButton.setOnAction(e -> sendCommand(CommandType.READY));
        pauseButton.setOnAction(e -> sendCommand(CommandType.PAUSE));
        resumeButton.setOnAction(e -> sendCommand(CommandType.RESUME));
        shootButton.setOnAction(e -> sendCommand(CommandType.SHOOT));
        stopButton.setOnAction(e -> sendCommand(CommandType.STOP));
        disconnectButton.setOnAction(e -> disconnect());

        setControlsDisabled(true);
    }

    private void setControlsDisabled(boolean disabled) {
        readyButton.setDisable(disabled);
        pauseButton.setDisable(disabled);
        resumeButton.setDisable(disabled);
        stopButton.setDisable(disabled);
        shootButton.setDisable(disabled);
    }

    private void connect() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) return;
        playerName = name;
        try {
            client = new GameClient("localhost", 12345, playerName, this);
            client.start();
            client.sendCommand(new CommandDTO(CommandType.CONNECT, playerName));
            connectButton.setDisable(true);
            nameField.setDisable(true);
            statusLabel.setText("Status: Connected");
            setControlsDisabled(false);
            startAnimation();
        } catch (Exception e) {
            statusLabel.setText("Status: Connection failed");
            e.printStackTrace();
        }
    }

    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GameStateDTO state = client.getLastState();
                if (state != null) {
                    updateState(state);
                }
            }
        };
        animationTimer.start();
    }

    private void disconnect() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
        if (client != null) {
            client.disconnect();
            client = null;
        }
        connectButton.setDisable(false);
        nameField.setDisable(false);
        setControlsDisabled(true);
        playersListView.getItems().clear();
        statusLabel.setText("Status: Disconnected");
        objectsLayer.getChildren().clear();
        targetViews.clear();
        arrowViews.clear();

        gamePane.getChildren().removeAll(playerViews.values());
        playerViews.clear();
    }

    private void sendCommand(CommandType type) {
        if (client != null) {
            client.sendCommand(new CommandDTO(type, playerName));
        }
    }

    public void updateState(GameStateDTO state) {
        playersListView.getItems().clear();
        for (PlayerDTO p : state.getPlayers()) {
            String line = p.getName() + "  Score:" + p.getScore() + " Shots:" + p.getShots() +
                    (p.isReady() ? " [READY]" : "");
            playersListView.getItems().add(line);
        }

        Set<Integer> currentIndices = new HashSet<>();
        for (PlayerDTO p : state.getPlayers()) {
            int idx = p.getId();
            currentIndices.add(idx);
            if (!playerViews.containsKey(idx)) {
                PlayerView view = new PlayerView(PLAYER_COLORS[idx % PLAYER_COLORS.length]);
                view.setLayoutX(60); // смещение внутри левой зоны
                view.setLayoutY(PLAYER_Y[idx % PLAYER_Y.length]);
                gamePane.getChildren().add(view);
                playerViews.put(idx, view);
            }
        }
        playerViews.keySet().removeIf(idx -> {
            if (!currentIndices.contains(idx)) {
                gamePane.getChildren().remove(playerViews.get(idx));
                return true;
            }
            return false;
        });

        Set<Integer> currentTargetIds = new HashSet<>();
        for (TargetDTO t : state.getTargets()) {
            currentTargetIds.add(t.getId());
            TargetView view = targetViews.get(t.getId());
            if (view == null) {
                view = new TargetView(t.getRadius());
                view.setPosition(t.getX(), t.getY());
                objectsLayer.getChildren().add(view);
                targetViews.put(t.getId(), view);
            } else {
                view.setPosition(t.getX(), t.getY());
            }
        }
        targetViews.keySet().removeIf(id -> {
            if (!currentTargetIds.contains(id)) {
                objectsLayer.getChildren().remove(targetViews.get(id));
                return true;
            }
            return false;
        });


        Set<Integer> currentArrowKeys = new HashSet<>();
        for (ArrowDTO a : state.getArrows()) {
            if (!a.isActive()) continue;
            int ownerIdx = a.getPlayerId();
            currentArrowKeys.add(ownerIdx);
            ArrowView view = arrowViews.get(ownerIdx);
            if (view == null) {
                view = new ArrowView(PLAYER_COLORS[ownerIdx % PLAYER_COLORS.length]);
                view.setPosition(a.getX(), a.getY());
                objectsLayer.getChildren().add(view);
                arrowViews.put(ownerIdx, view);
            } else {
                view.setPosition(a.getX(), a.getY());
            }
        }
        arrowViews.keySet().removeIf(key -> {
            if (!currentArrowKeys.contains(key)) {
                objectsLayer.getChildren().remove(arrowViews.get(key));
                return true;
            }
            return false;
        });


        String statusText = "Status: " + state.getState();
        if (state.getWinnerName() != null) {
            statusText += "  Winner: " + state.getWinnerName();
        }
        statusLabel.setText(statusText);
    }

    public void connectionRefused(String message) {
        statusLabel.setText("Status: " + message);
        connectButton.setDisable(false);
        nameField.setDisable(false);
        setControlsDisabled(true);
    }

    public BorderPane getRoot() { return root; }
}
