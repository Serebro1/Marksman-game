package org.example.marksmangame.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class ArrowView extends Group {
    public ArrowView() {
        Line shaft = new Line(0, 0, 50, 0);
        shaft.setStrokeWidth(4);
        shaft.setStroke(Color.SADDLEBROWN);

        Polygon head = new Polygon(
                50, -6,
                65, 0,
                50, 6
        );
        head.setFill(Color.DARKGRAY);

        getChildren().addAll(shaft, head);
    }

    public void setPosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }
}
