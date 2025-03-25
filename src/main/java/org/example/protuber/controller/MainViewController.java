package org.example.protuber.controller;
import javafx.scene.control.Button;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.example.protuber.Main;
import org.example.protuber.model.Video;
import org.example.protuber.service.VideoService;
import org.example.protuber.storage.VideoStorage;
import org.example.protuber.utils.MediaValidator;
import org.example.protuber.utils.YTDLHelper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainViewController implements Initializable {
    private final Map<String, Node> contentCache = new HashMap<>();
    private VideoPlayerController videoPlayerController;
    private NoteController noteController;
    private HomeViewController homeViewController;
    private VideoService videoService;
    @FXML
    private Pane contentView;
    @FXML
    private TextField urlTextField;
    @FXML
    private Label addStatus;
    @FXML
    private Button addBtn;
    @FXML
    private ProgressBar progressbar;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        switchMainView();
    }
    private void startDownload(String videoUrl, Consumer<String> onComplete) {
        addBtn.setDisable(true);
        addStatus.setTextFill(Color.YELLOW);
        addStatus.setText("Video đang được tải...");

        progressbar.progressProperty().unbind();
        progressbar.setProgress(0.0);

        Task<String> downloadTask = new Task<>() {
            @Override
            protected String call() {
                return YTDLHelper.getDirectVideoUrl(videoUrl, progress -> Platform.runLater(() -> updateProgress(progress, 100)));
            }
        };

        progressbar.progressProperty().bind(downloadTask.progressProperty());

        downloadTask.setOnSucceeded(event -> {
            addBtn.setDisable(false);
            String directUrl = downloadTask.getValue();
            if (directUrl != null) {
                onComplete.accept(directUrl);
            } else {
                addStatus.setTextFill(Color.RED);
                addStatus.setText("❌ Link không hợp lệ!");
            }
        });

        downloadTask.setOnFailed(event -> {
            addBtn.setDisable(false);
            addStatus.setTextFill(Color.RED);
            addStatus.setText("❌ Tải xuống thất bại!");
        });

        new Thread(downloadTask).start();
    }

    public void handleAddVideo() {
        String oringalUrl = urlTextField.getText().trim();
        if(VideoService.hasVideo(oringalUrl)) {
            processDownloadedVideo(oringalUrl, oringalUrl);
        }else {
            if (!MediaValidator.isLocalUrl(oringalUrl)) {
                startDownload(oringalUrl, directUrl -> Platform.runLater(() -> processDownloadedVideo(directUrl, oringalUrl)));
            } else {
                processDownloadedVideo(oringalUrl, oringalUrl);
            }
        }
    }

    private void processDownloadedVideo(String localUrl, String originalUrl) {
        this.videoService = new VideoService(homeViewController);

        Task<Boolean> processTask = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    boolean result = videoService.addVideo(localUrl, originalUrl);
                    updateProgress(1, 1);
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };

        progressbar.progressProperty().bind(processTask.progressProperty());

        processTask.setOnSucceeded(event -> {
            boolean result = processTask.getValue();
            Platform.runLater(() -> {
                if (result) {
                    addStatus.setTextFill(Color.GREEN);
                    addStatus.setText("✅ Video đã được thêm vào danh sách!");
                } else {
                    addStatus.setTextFill(Color.RED);
                    addStatus.setText("❌ Video đã tồn tại hoặc URL không hợp lệ!");
                }
                addBtn.setDisable(false);
            });
        });

        processTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                addStatus.setTextFill(Color.RED);
                addStatus.setText("❌ Lỗi khi xử lý video!");
                addBtn.setDisable(false);
            });
        });

        new Thread(processTask).start();
    }




    public void switchMainView() {

        switchContentView("view/HomeView.fxml");
        homeViewController.getVideoListVBox().getChildren().clear();
        List<Video> myListVideo = VideoStorage.loadVideos();
        for (Video video : myListVideo) {
            homeViewController.addVideoToUI(video);
        }
    }

    public void switchNoteView() {
        switchContentView("view/NoteView.fxml");
        noteController.refreshTable();
    }

    public void switchVideoPlayerView(String videoUrl) {
        switchContentView("view/VideoPlayerView.fxml");
        videoPlayerController.playVideo(videoUrl);
    }

    public void switchContentView(String fxmlFile) {
        try {
            addStatus.setText("");
            Node newContent = contentCache.get(fxmlFile);
            if (newContent == null) {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
                newContent = loader.load();
                contentCache.put(fxmlFile, newContent); // Lưu vào cache
                if (fxmlFile.equals("view/VideoPlayerView.fxml")) {
                    videoPlayerController = loader.getController();
                } else if (fxmlFile.equals("view/HomeView.fxml")) {
                    homeViewController = loader.getController();
                    homeViewController.setMainViewController(this);
                } else if(fxmlFile.equals("view/NoteView.fxml")){
                  noteController =  loader.getController();
                }
            }

            contentView.getChildren().setAll(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
