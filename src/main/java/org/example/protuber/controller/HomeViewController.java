package org.example.protuber.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.protuber.Main;
import org.example.protuber.model.Video;
import org.example.protuber.storage.VideoStorage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeViewController implements Initializable {
    @FXML
    private VBox videoListVBox;
    private MainViewController mainViewController = new MainViewController();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void addVideoToUI(Video video) {
        try {
            FXMLLoader loader = new FXMLLoader(HomeViewController.class.getResource("/org/example/protuber/view/VideoItemView.fxml"));
            HBox videoItem = loader.load();
            VideoItemController controller = loader.getController();
            controller.setHomeViewController(this);
            controller.setVideoData(video);
            videoListVBox.getChildren().add(0, videoItem);  // Thêm vào đầu danh sách
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playVideo(String videoUrl) {
            mainViewController.switchVideoPlayerView(videoUrl);
    }
    public VBox getVideoListVBox() {
        return videoListVBox;
    }
}
