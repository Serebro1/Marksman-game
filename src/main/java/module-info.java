module org.example.marksmangame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.naming;

    opens org.example.marksmangame to javafx.fxml;
    opens org.example.marksmangame.db.entity to org.hibernate.orm.core;
    exports org.example.marksmangame;
}