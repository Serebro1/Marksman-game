package org.example.marksmangame.desktopclient.view;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.marksmangame.dto.LeaderboardDTO;
import org.example.marksmangame.dto.LeaderboardEntryDTO;

import java.util.List;

public class LeaderboardView {

    private final Stage stage;
    private final TableView<LeaderboardEntryDTO> table;

    public LeaderboardView() {
        table = new TableView<>();

        TableColumn<LeaderboardEntryDTO, String> nameCol =
                new TableColumn<>("Имя");
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().username()));

        TableColumn<LeaderboardEntryDTO, Integer> winsCol =
                new TableColumn<>("Победы");
        winsCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().wins()).asObject());

        table.getColumns().addAll(List.of(nameCol, winsCol));

        stage = new Stage();
        stage.setTitle("Leaderboard");
        stage.setScene(new Scene(new VBox(table), 300, 400));
    }

    public void show(LeaderboardDTO dto) {
        table.getItems().setAll(dto.entries());

        if (!stage.isShowing()) {
            stage.show();
        } else {
            stage.toFront();
        }
    }

    public boolean isShowing() {
        return stage.isShowing();
    }
}
