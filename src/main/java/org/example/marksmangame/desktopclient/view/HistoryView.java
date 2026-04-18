package org.example.marksmangame.desktopclient.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.marksmangame.dto.GameHistoryDTO;
import org.example.marksmangame.dto.GameHistoryEntryDTO;

import java.time.LocalDateTime;
import java.util.List;

public class HistoryView {

    private final Stage stage;
    private final TableView<GameHistoryEntryDTO> table;

    public HistoryView() {
        table = new TableView<>();

        TableColumn<GameHistoryEntryDTO, String> nameCol =
                new TableColumn<>("Игра");
        nameCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().gameName()));

        TableColumn<GameHistoryEntryDTO, String> winnerCol =
                new TableColumn<>("Победитель");
        winnerCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().winner()));

        TableColumn<GameHistoryEntryDTO, String> startCol =
                new TableColumn<>("Начало");
        startCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().startedAt().toString()));

        TableColumn<GameHistoryEntryDTO, String> endCol =
                new TableColumn<>("Конец");
        endCol.setCellValueFactory(c -> {
            LocalDateTime finishedAt = c.getValue().finishedAt();
            String value = (finishedAt != null)
                    ? finishedAt.toString()
                    : "Не завершена";
            return new SimpleStringProperty(value);
        });

        table.getColumns().addAll(List.of(nameCol, winnerCol, startCol, endCol));

        stage = new Stage();
        stage.setTitle("Game History");
        stage.setScene(new Scene(new VBox(table), 500, 400));
    }

    public void show(GameHistoryDTO dto) {
        table.getItems().setAll(dto.games());

        if (!stage.isShowing()) {
            stage.show();
        } else {
            stage.toFront();
        }
    }
}
