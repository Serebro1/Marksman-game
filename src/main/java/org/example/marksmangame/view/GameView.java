package org.example.marksmangame.view;

import org.example.marksmangame.controllers.Engine;
import org.example.marksmangame.controllers.GameLoop;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.example.marksmangame.model.Target;

import java.util.HashMap;
import java.util.Map;

public class GameView {
    private final double width, height;
    private final BorderPane root = new BorderPane();
    private final Pane gamePane = new Pane();
    private final Pane objectsLayer = new Pane();

    private final Engine engine = new Engine();
    private GameLoop loop;

    private final Label scoreLabel = new Label("Score: 0");
    private final Label shotsLabel = new Label("Shots: 0");

    private final Map<Target, TargetView> targetViews = new HashMap<>();
    private ArrowView arrowView;

    public GameView() {

        width = 1000;
        height = 600;

        gamePane.setPrefSize(width, height);
        // game objects layer
        gamePane.getChildren().add(objectsLayer);

        // Player
        PlayerView playerView = new PlayerView(height);
        gamePane.getChildren().add(playerView);

        // target guideline
        Line guideNear = new Line(700, 0, 700, height);
        guideNear.setStroke(Color.GRAY);

        Line guideFar = new Line(850, 0, 850, height);
        guideFar.setStroke(Color.GRAY);

        gamePane.getChildren().addAll(guideNear, guideFar);

        createScoreShotsPanel();
        createControlPanel();
        createObservers();

        root.setCenter(gamePane);
    }

    private void createScoreShotsPanel() {
        VBox statsBox = new VBox(10, scoreLabel, shotsLabel);
        statsBox.setAlignment(Pos.CENTER_RIGHT);
        statsBox.setStyle("""
                -fx-background-color: white;
                -fx-padding: 10;
                -fx-border-color: black;
                """);
        statsBox.setLayoutX(900);
        statsBox.setLayoutY(500);

        gamePane.getChildren().add(statsBox);
    }

    private void createControlPanel() {
        // Buttons
        Button start = new Button("Start");
        Button stop = new Button("Stop");
        Button pause = new Button("Pause");
        Button resume = new Button("Resume");
        Button shoot = new Button("Shoot");

        HBox controlsBox = new HBox(10, start, stop, pause, resume, shoot);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setStyle("""
                -fx-background-color: lightgray;
                -fx-padding: 10;
                -fx-border-color: black;
                """);
        controlsBox.setLayoutX(150);
        controlsBox.setLayoutY(555);

        gamePane.getChildren().add(controlsBox);

        // actions on buttons
        start.setOnAction(e -> {
            engine.start();

            objectsLayer.getChildren().clear();
            targetViews.clear();
            arrowView = null;

            createTargetViews();

            if (loop != null) loop.stopLoop();
            loop = new GameLoop(engine, width, height);
            loop.start();
        });

        stop.setOnAction(e -> {
            engine.stop();
            if (loop != null) loop.stopLoop();
        });
        pause.setOnAction(e -> {
            engine.pause();
            if (loop != null) loop.pauseLoop();
        });
        resume.setOnAction(e -> {
            engine.resume();
            if (loop != null) loop.resumeLoop();
        });

        shoot.setOnAction(e -> engine.shoot());
    }

    private void createObservers() {
        // statistics observers
        engine.getPlayer().addScoreObserver(
                score -> Platform.runLater(() ->
                        scoreLabel.setText("Score: " + score))
        );

        engine.getPlayer().addShotsObserver(
                shots -> Platform.runLater(() ->
                        shotsLabel.setText("Shots: " + shots))
        );

        // arrow observers
        engine.addArrowCreatedObserver(arrow ->
                Platform.runLater(() -> createArrowView(arrow)));

        engine.addArrowDestroyedObserver(() ->
                Platform.runLater(this::removeArrowView));
    }

    private void createTargetViews() {

        for (Target target : engine.getTargets()) {

            TargetView view = new TargetView(target.getRadius());
            view.setPosition(target.getX(), target.getY());

            target.addPositionObserver((x, y) ->
                    Platform.runLater(() -> {
                        if (view.getParent() != null) {
                            view.setPosition(x, y);
                        }
                    }));

            targetViews.put(target, view);
            objectsLayer.getChildren().add(view);
        }
    }

    private void createArrowView(org.example.marksmangame.model.Arrow arrow) {

        ArrowView view = new ArrowView();
        view.setPosition(arrow.getX(), arrow.getY());

        arrow.addPositionObserver((x, y) ->
                Platform.runLater(() -> {
                    if (view.getParent() != null) {
                        view.setPosition(x, y);
                    }
                }));

        arrowView = view;
        objectsLayer.getChildren().add(view);
    }

    private void removeArrowView() {
        if (arrowView != null) {
            objectsLayer.getChildren().remove(arrowView);
            arrowView = null;
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
