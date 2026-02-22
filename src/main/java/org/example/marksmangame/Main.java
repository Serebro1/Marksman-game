package org.example.marksmangame;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.marksmangame.view.GameView;
import javafx.scene.Scene;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        GameView view = new GameView();
        Scene scene = new Scene(view.getRoot(), 1000, 600);

        stage.setTitle("Marksman Game");
        stage.setScene(scene);
        stage.setResizable(false);

        stage.show();
    }
}
