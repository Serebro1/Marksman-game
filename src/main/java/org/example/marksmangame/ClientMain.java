package org.example.marksmangame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.marksmangame.client.GameClient;
import org.example.marksmangame.client.GameClientView;

public class ClientMain extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameClientView view = new GameClientView();
        Scene scene = new Scene(view.getRoot(), 1200, 600);
        primaryStage.setTitle("Marksman Game Client");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            GameClient client = view.getClient();
            if (client != null) {
                client.disconnect();
            }
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
