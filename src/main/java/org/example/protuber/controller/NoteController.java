package org.example.protuber.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.example.protuber.model.Video;
import org.example.protuber.storage.VideoStorage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NoteController {

    @FXML
    private TableView<Video> videoTable;
    @FXML
    private TableColumn<Video, String> titleColumn;
    @FXML
    private TableColumn<Video, String> tagColumn;
    @FXML
    private TableColumn<Video, String> noteColumn;
    @FXML
    private TableColumn<Video, Void> actionColumn;
    @FXML
    private TableColumn<Video, Integer> sttColumn;

    private final ObservableList<Video> videoList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
        noteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));

        videoTable.setEditable(true);
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tagColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        noteColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        titleColumn.setOnEditCommit(event -> {
            Video video = event.getRowValue();
            video.setTitle(event.getNewValue());
            VideoStorage.saveVideos(videoList); // Lưu lại danh sách
        });
        tagColumn.setOnEditCommit(event -> {
            Video video = event.getRowValue();
            video.setTag(event.getNewValue());
            VideoStorage.saveVideos(videoList);
        });
        noteColumn.setOnEditCommit(event -> {
            Video video = event.getRowValue();
            video.setNote(event.getNewValue());
            VideoStorage.saveVideos(videoList);
        });

        sttColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(videoTable.getItems().indexOf(cellData.getValue()) + 1)
        );

        // Thêm cột chứa nút Sửa/Xóa
        addActionButtons();

        // Gán dữ liệu vào TableView
        videoTable.setItems(videoList);

        // Load dữ liệu ban đầu
        refreshTable();
    }


    private void addActionButtons() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Video, Void> call(final TableColumn<Video, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button("Sửa");
                    private final Button deleteButton = new Button("Xóa");

                    {
                        editButton.setOnAction(event -> editVideo(getIndex()));
                        deleteButton.setOnAction(event -> deleteVideo(getIndex()));
                    }

                    private final HBox buttonGroup = new HBox(5, editButton, deleteButton);

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttonGroup);
                        }
                    }
                };
            }
        });
    }


    private void editVideo(int index) {
        Video video = videoList.get(index);

        // Tạo dialog tùy chỉnh
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Sửa Video");
        dialog.setHeaderText("Chỉnh sửa ghi chú video:");

        // Thêm nút OK và Cancel
        ButtonType saveButtonType = new ButtonType("Lưu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Tạo TextArea thay vì TextField
        TextArea noteArea = new TextArea(video.getNote());
        noteArea.setWrapText(true);
        noteArea.setPrefSize(400, 200); // Tăng kích thước để nhập dễ dàng hơn

        // Thêm TextArea vào Dialog
        dialog.getDialogPane().setContent(noteArea);

        // Xử lý khi nhấn "Lưu"
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return noteArea.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newNote -> {
            video.setNote(newNote);
            VideoStorage.saveVideos(videoList); // Lưu lại danh sách
            videoTable.refresh();
        });
    }



    private void deleteVideo(int index) {
        Video video = videoList.get(index);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa video này không?",
                ButtonType.YES, ButtonType.NO);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                videoList.remove(video);
                boolean removed = videoList.remove(video);
                try {
                    Files.delete(Paths.get(video.getLocalUrl()));
                    System.out.println("File deleted successfully.");
                } catch (Exception e) {
                    System.err.println("Failed to delete file: " + e.getMessage());
                }
                VideoStorage.saveVideos(videoList); // Cập nhật file JSON
            }
        });
    }


    public void refreshTable() {
        List<Video> videos = VideoStorage.loadVideos();
        videoList.setAll(videos);
    }
}
