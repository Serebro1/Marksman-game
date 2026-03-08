package org.example.marksmangame.client.view;

import javafx.scene.Group;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TargetView extends Group {
    private final Circle border;
    private final Circle center;

    public TargetView(double radius) {
        border = new Circle(radius);
        border.setFill(Color.WHITE);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(3);

        center = new Circle(radius / 2);
        center.setFill(Color.RED);
        center.setEffect(new GaussianBlur(10));

        getChildren().addAll(border, center);
    }

    public void setPosition(double x, double y) {
        setLayoutX(x);
        setLayoutY(y);
    }
}
