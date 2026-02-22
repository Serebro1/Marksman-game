module org.example.marksmangame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.marksmangame to javafx.fxml;
    exports org.example.marksmangame;
}