package org.example.protuber.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.protuber.model.Video;
import org.example.protuber.storage.VideoStorage;
import org.example.protuber.service.VideoService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VideoItemController implements Initializable {

    @FXML private Label titleLabel, tagLabel;
    @FXML private HBox videoItem;
    @FXML private Button editButton;
    @FXML private TextField titleField;
    @FXML private StackPane titleContainer, tagContainer;
    @FXML private TextField tagField;
    private HomeViewController homeViewController;
    private Video video;
    private boolean isEditing = false;
    private List<Video> videos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleField = new TextField();
        titleField.setVisible(false);
        tagField = new TextField();
        tagField.setVisible(false);
        titleContainer.getChildren().add(titleField);
        tagContainer.getChildren().add(tagField);
    }

    public void setHomeViewController(HomeViewController homeViewController) {
        this.homeViewController = homeViewController;
    }

    public void setVideoData(Video video) {
        this.video = video;
        titleLabel.setText(video.getTitle() != null ? video.getTitle() : "Video " + video.getId());
        tagLabel.setText(video.getTag() != null ? video.getTag() : "Tag " + video.getId());
    }


    public void playVideo() {
        homeViewController.playVideo(video.getLocalUrl());
        System.out.println("Playing: " + video.getTitle());
    }

    public void editVideo() {
        if (isEditing) {
            video.setTitle(titleField.getText());
            titleLabel.setText(video.getTitle());

            video.setTag(tagField.getText());
            tagLabel.setText(video.getTag());
            videos = VideoStorage.loadVideos();
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getId().equals(video.getId())) {
                    videos.set(i, video);
                    break;
                }
            }
            VideoStorage.saveVideos(videos);

            titleLabel.setVisible(true);
            tagLabel.setVisible(true);
            titleField.setVisible(false);
            tagField.setVisible(false);
            editButton.setText("✏ Chỉnh sửa");
        } else {
            titleField.setText(titleLabel.getText());
            tagField.setText(tagLabel.getText());
            titleLabel.setVisible(false);
            titleField.setVisible(true);
            tagLabel.setVisible(false);
            tagField.setVisible(true);

            editButton.setText("💾 Lưu");
        }
        isEditing = !isEditing;
    }

    public void removeVideo() {
        System.out.println("Removing: " + video.getTitle());
        VideoService.getInstance().removeVideo(video);
        ((VBox) videoItem.getParent()).getChildren().remove(videoItem);
    }
}
