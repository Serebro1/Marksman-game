package org.example.marksmangame.desktopclient.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class PlayerView extends Group {
    public PlayerView(Color color) {
        Polygon player = new Polygon(
                0, -15,
                0, 15,
                30, 0
        );

        player.setFill(color);
        player.setStroke(Color.BLACK);
        getChildren().addAll(player);
    }
}
