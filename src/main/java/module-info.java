module org.example.marksmangame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires org.slf4j;
    requires com.google.gson;

    opens org.example.marksmangame to javafx.fxml;
    opens org.example.marksmangame.server.db.entity to org.hibernate.orm.core;
    opens org.example.marksmangame.network to com.google.gson;
    opens org.example.marksmangame.dto to com.google.gson;
    exports org.example.marksmangame;
    opens org.example.marksmangame.server to com.google.gson;
}