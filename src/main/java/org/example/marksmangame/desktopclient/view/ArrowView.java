package org.example.marksmangame.desktopclient.view;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class ArrowView extends Group {
    private double OFFSET_X = 31;

    public ArrowView(Color color) {
        Line shaft = new Line(0, 0, 50, 0);
        shaft.setStrokeWidth(4);
        shaft.setStroke(Color.SADDLEBROWN);

        Polygon head = new Polygon(
                50, -6,
                65, 0,
                50, 6
        );
        head.setFill(color);

        getChildren().addAll(shaft, head);
    }

    public void setPosition(double x, double y) {
        setLayoutX(x - OFFSET_X);
        setLayoutY(y);
    }
}
