package org.example.marksmangame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.marksmangame.client.GameClientView;

public class ClientMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameClientView view = new GameClientView();
        Scene scene = new Scene(view.getRoot(), 1200, 600);
        primaryStage.setTitle("Marksman Game Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
