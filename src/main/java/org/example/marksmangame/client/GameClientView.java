package org.example.marksmangame.client;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.marksmangame.client.view.ArrowView;
import org.example.marksmangame.client.view.PlayerView;
import org.example.marksmangame.client.view.TargetView;
import org.example.marksmangame.dto.*;

import java.util.*;

public class GameClientView {
    private final double WIDTH = 2000;
    private final double HEIGHT = 600;
    private static final Color[] PLAYER_COLORS = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE};
    private static final double[] PLAYER_Y = {200, 300, 400, 500};

    private final BorderPane root = new BorderPane();
    private final Pane gamePane = new Pane();
    private final Pane objectsLayer = new Pane();
    private final ListView<String> playersListView = new ListView<>();
    private final Label statusLabel = new Label("Status: Disconnected");

    private final Button connectButton = new Button("Connect");
    private final Button readyButton = new Button("Ready");
    private final Button pauseButton = new Button("Pause");
    private final Button stopButton = new Button("Stop");
    private final Button shootButton = new Button("Shoot");
    private final Button disconnectButton = new Button("Disconnect");
    private final Button leaderboardButton = new Button("Leaderboard");
    private final Button historyButton = new Button("History");

    private final TextField nameField = new TextField("Player");

    private GameClient client;
    private String playerName;
    private AnimationTimer timer;

    private final Map<Integer, TargetView> targetViews = new HashMap<>();
    private final Map<Integer, ArrowView> arrowViews = new HashMap<>();
    private final Map<Integer, PlayerView> playerViews = new HashMap<>();


    public GameClientView() { setupUI(); }

    private void setupUI() {
        gamePane.setPrefSize(WIDTH, HEIGHT);
        gamePane.getChildren().add(objectsLayer);

        Rectangle zone = new Rectangle(150, HEIGHT);
        zone.setFill(Color.LIGHTYELLOW);
        zone.setStroke(Color.BLACK);

        Line guideNear = new Line(700, 0, 700, HEIGHT);
        guideNear.setStroke(Color.GRAY);
        Line guideFar = new Line(850, 0, 850, HEIGHT);
        guideFar.setStroke(Color.GRAY);
        gamePane.getChildren().addAll(zone, guideNear, guideFar);

        // панель управления
        HBox controlBox = new HBox(10, readyButton, pauseButton, shootButton, stopButton, leaderboardButton, historyButton, disconnectButton);
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

        connectButton.setOnAction(_ -> connect());
        readyButton.setOnAction(_ -> sendCommand(CommandType.READY));
        pauseButton.setOnAction(_ -> sendCommand(CommandType.PAUSE));
        shootButton.setOnAction(_ -> sendCommand(CommandType.SHOOT));
        stopButton.setOnAction(_ -> sendCommand(CommandType.STOP));
        leaderboardButton.setOnAction(_ -> sendCommand(CommandType.LEADERBOARD));
        historyButton.setOnAction(_ -> sendCommand(CommandType.HISTORY));
        disconnectButton.setOnAction(_ -> disconnect());


        setControlsDisabled(true);
    }

    private void setControlsDisabled(boolean disabled) {
        readyButton.setDisable(disabled);
        pauseButton.setDisable(disabled);
        stopButton.setDisable(disabled);
        shootButton.setDisable(disabled);
        disconnectButton.setDisable(disabled);
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
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                GameClient localClient = client;
                if (localClient == null) return;

                GameStateDTO state = localClient.getLastState();
                if (state != null) { updateState(state); }
            }
        };
        timer.start();
    }

    private void disconnect() {
        if (timer != null) { timer.stop(); timer = null; }
        if (client != null) { client.disconnect(); client = null; }

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
        if (client != null) { client.sendCommand(new CommandDTO(type, playerName)); }
    }

    public void updateState(GameStateDTO state) {
        updatePlayersList(state);
        updatePlayerViews(state);
        updateTargets(state);
        updateArrows(state);
        updateStatus(state);
    }

    private void updatePlayersList(GameStateDTO state) {
        playersListView.getItems().clear();

        for (PlayerDTO p : state.players()) {
            String suffix = buildPlayerStatusSuffix(p, state);

            String line = p.name()
                    + "  Score:" + p.score()
                    + " Shots:" + p.shots()
                    + suffix;

            playersListView.getItems().add(line);
        }
    }
    private String buildPlayerStatusSuffix(PlayerDTO p, GameStateDTO state) {
        if (state.state() == GameState.PAUSED && p.name().equals(state.pausedBy())) {
            return " [PAUSED]";
        }
        if (p.ready()) { return " [READY]"; }
        return "";
    }

    private void updatePlayerViews(GameStateDTO state) {
        Set<Integer> currentIds = new HashSet<>();
        for (PlayerDTO p : state.players()) {
            int id = p.id();
            currentIds.add(id);
            if (!playerViews.containsKey(id)) {
                PlayerView view = new PlayerView(PLAYER_COLORS[id % PLAYER_COLORS.length]);
                view.setLayoutX(60);
                view.setLayoutY(PLAYER_Y[id % PLAYER_Y.length]);
                gamePane.getChildren().add(view);
                playerViews.put(id, view);
            }
        }
        removeMissing(playerViews, currentIds, gamePane);
    }

    private void updateTargets(GameStateDTO state) {
        Set<Integer> currentIds = new HashSet<>();
        for (TargetDTO t : state.targets()) {
            currentIds.add(t.id());
            TargetView view = targetViews.get(t.id());
            if (view == null) {
                view = new TargetView(t.radius());
                objectsLayer.getChildren().add(view);
                targetViews.put(t.id(), view);
            }
            view.setPosition(t.x(), t.y());
        }
        removeMissing(targetViews, currentIds, objectsLayer);
    }

    private void updateArrows(GameStateDTO state) {
        Set<Integer> currentKeys = new HashSet<>();
        for (ArrowDTO a : state.arrows()) {
            if (!a.active()) continue;
            int owner = a.playerId();
            currentKeys.add(owner);
            ArrowView view = arrowViews.get(owner);
            if (view == null) {
                view = new ArrowView(PLAYER_COLORS[owner % PLAYER_COLORS.length]);
                objectsLayer.getChildren().add(view);
                arrowViews.put(owner, view);
            }
            view.setPosition(a.x(), a.y());
        }
        removeMissing(arrowViews, currentKeys, objectsLayer);
    }

    private <T extends Node> void removeMissing(
            Map<Integer, T> views,
            Set<Integer> activeIds,
            Pane layer
    ) {
        views.keySet().removeIf(id -> {
            if (!activeIds.contains(id)) {
                layer.getChildren().remove(views.get(id));
                return true;
            }
            return false;
        });
    }

    private void updateStatus(GameStateDTO state) {
        String status = "Status: " + state.state();
        if (state.winnerName() != null) {
            status += "  Winner: " + state.winnerName();
        }
        statusLabel.setText(status);
    }

    public void showLeaderboard(LeaderboardDTO dto) {

        TableView<LeaderboardEntryDTO> table = new TableView<>();

        TableColumn<LeaderboardEntryDTO, String> nameCol = new TableColumn<>("Имя");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().username()));

        TableColumn<LeaderboardEntryDTO, Integer> winsCol = new TableColumn<>("Победы");
        winsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().wins()).asObject());

        table.getColumns().addAll(List.of(nameCol, winsCol));
        table.getItems().setAll(dto.entries());

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Leaderboard");
            stage.setScene(new Scene(new VBox(table), 300, 400));
            stage.show();
        });
    }

    public void showHistory(GameHistoryDTO dto) {

        TableView<GameHistoryEntryDTO> table = new TableView<>();

        TableColumn<GameHistoryEntryDTO, String> nameCol =
                new TableColumn<>("Игра");
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().gameName()));

        TableColumn<GameHistoryEntryDTO, String> winnerCol =
                new TableColumn<>("Победитель");
        winnerCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().winner()));

        TableColumn<GameHistoryEntryDTO, String> startCol =
                new TableColumn<>("Начало");
        startCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().startedAt().toString()));

        TableColumn<GameHistoryEntryDTO, String> endCol =
                new TableColumn<>("Конец");
        endCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().finishedAt().toString()));

        table.getColumns().addAll(List.of(nameCol, winnerCol, startCol, endCol));
        table.getItems().setAll(dto.games());

        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Game History");
            stage.setScene(new Scene(new VBox(table), 500, 400));
            stage.show();
        });
    }

    public void connectionRefused(String message) {
        statusLabel.setText("Status: " + message);
        connectButton.setDisable(false);
        nameField.setDisable(false);
        setControlsDisabled(true);
    }

    public BorderPane getRoot() { return root; }
    public GameClient getClient() { return client; }
}
