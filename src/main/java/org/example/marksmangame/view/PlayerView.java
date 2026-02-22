package org.example.marksmangame.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class PlayerView extends Group {
    public PlayerView(double height) {
        Rectangle zone = new Rectangle(150, height);
        zone.setFill(Color.LIGHTYELLOW);
        zone.setStroke(Color.BLACK);

        Line sep = new Line(150, 0, 150, height);
        sep.setStrokeWidth(3);

        Polygon player = new Polygon(
                60, height / 2 - 25,
                60, height / 2 + 25,
                120, height / 2
        );

        player.setFill(Color.BLUE);
        getChildren().addAll(zone, sep, player);
    }
}
