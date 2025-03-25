package org.example.protuber.controller;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.protuber.model.Timestamp;
import org.example.protuber.model.Video;
import org.example.protuber.service.VideoService;
import org.example.protuber.storage.VideoStorage;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class VideoPlayerController implements Initializable {
    @FXML
    private MediaView mediaView;
    @FXML
    private VBox videoControls;
    @FXML
    private Pane videoContainer;
    @FXML
    private Region videoRegion;
    //component video
    @FXML
    private Button playVideoPlayer;
    @FXML
    private ChoiceBox <Double> speedBox;
    @FXML
    private Slider progressBarVideo, volumeSlider;

    @FXML
    private TextArea noteArea;

    @FXML
    private Button editButtonNote;

    @FXML
    private Label titleLabel;

    @FXML
    private Label currentTimeLabel;

    @FXML
    private Label totalTimeLabel;    // Thời gian tổng

    private Media media;
    private MediaPlayer mediaPlayer;
    private String url;
    private List<Video> videos;
    private Video video;
    private boolean isInteracting = false;
    private List<Timestamp> timestamps;// Danh sách tạm thời

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mediaView.fitWidthProperty().bind(videoContainer.widthProperty());
        mediaView.fitHeightProperty().bind(videoContainer.heightProperty());
        StackPane.setAlignment(mediaView, Pos.CENTER);

        videoRegion.setMouseTransparent(true);

        mediaView.sceneProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {

                stopVideo();
            }

        });
        videoControls.setVisible(false);
        videoControls.setOpacity(0);


        videoContainer.setOnMouseEntered(event -> {
            videoControls.setVisible(true);
            fadeIn(videoControls);
        });

        videoContainer.setOnMouseExited(event -> {
            fadeOut(videoControls);
        });

        videoControls.setOnMouseEntered(event -> {
            videoControls.setVisible(true);
            fadeIn(videoControls);
        });

        noteArea.setEditable(false);
    }
    private void fadeIn(VBox controls) {
        controls.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), controls);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void fadeOut(VBox controls) {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED || isInteracting) {
            return;
        }
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), controls);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> controls.setVisible(false));
        fadeOut.play();
    }
    public void  setUpVolumeSlider(){
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> { mediaPlayer.setVolume(newValue.doubleValue() ); });
        volumeSlider.setValue(0.5);mediaPlayer.setVolume(0.5);
    }
    //Set up progressBarVideo
    public void setUpProgressBarVideo(){
        System.out.println("Media player ready");
        double totalDuration = mediaPlayer.getTotalDuration().toSeconds();
        System.out.println("Total duration: " + totalDuration);
        progressBarVideo.setMax(totalDuration);

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!progressBarVideo.isValueChanging()) {
                progressBarVideo.setValue(newValue.toSeconds());
            }
        });

        progressBarVideo.setOnMouseReleased(event -> {
            double seekTime = progressBarVideo.getValue();
            mediaPlayer.seek(Duration.seconds(seekTime));
        });
    }
    public void playVideo(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            System.out.println("Video URL is empty");
            return;
        }
        videoUrl = videoUrl.trim();
        System.out.println("videoUrl: " + videoUrl);
        playMediaVideo(videoUrl);
        setTitle(videoUrl);

        videos = VideoStorage.loadVideos();
        System.out.println(videos);
        video = VideoService.getVideoByUrl(videoUrl);
        System.out.println(video);
        timestamps = video.getTimestampList();
        System.out.println(timestamps
        );
        loadUiFromVideo();
    }

    public void setTitle(String videoUrl) {
        VideoService videoService = new VideoService();
        Video video = videoService. getVideoByUrl(videoUrl);
        titleLabel.setText(video.getTitle());
    }
    //Play
    public void playMediaVideo(String videoUrl) {
        videoUrl = videoUrl.trim();
        url = videoUrl;
        if (videoUrl.isEmpty() ) {
            System.out.println("videoUrl is empty");
            return;
        } else {
            if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
                System.out.println("pass 1");
                media = new Media(videoUrl);
            } else {
                File file = new File(videoUrl);
                System.out.println("pass 2");
                if (!file.exists()) {
                    System.out.println("File doesn't exist");
                    return;
                }
                media = new Media(file.toURI().toString());
            }

            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
        }
        mediaView.setOnMouseClicked(event -> {
            togglePlayPause();
        });

        // Ngăn controls bị ẩn khi đang chỉnh tốc độ hoặc âm lượng
        speedBox.setOnMouseEntered(event -> isInteracting = true);
        speedBox.setOnMouseExited(event -> isInteracting = false);

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            String formattedCurrentTime = formatDuration(newValue);
            currentTimeLabel.setText(formattedCurrentTime);
        });

        videoControls.setVisible(true);

        // Thiết lập sự kiện khi video bắt đầu phát
        mediaPlayer.setOnReady(() -> {
            System.out.println("Media player ready");
            setUpProgressBarVideo();
            setUpVolumeSlider();
            Duration totalDuration = mediaPlayer.getTotalDuration();
            totalTimeLabel.setText(formatDuration(totalDuration));
            mediaPlayer.play(); // Chỉ phát khi đã READY

        });
    }


    //Pause và resume
    public void togglePlayPause() {
        MediaPlayer.Status status = mediaPlayer.getStatus();
        if (status == MediaPlayer.Status.PLAYING) {
            videoControls.setVisible(true);
            fadeIn(videoControls);
            mediaPlayer.pause();
            playVideoPlayer.setText("▶");
        } else if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY) {
            mediaPlayer.play();
            playVideoPlayer.setText("⏸");
        }
    }

    //Reset
    public void resetVideo() {
        mediaPlayer.seek(Duration.ZERO);
        mediaPlayer.play();
    }
    public void stopVideo() {
        mediaPlayer.stop();
        mediaPlayer.dispose();
        mediaPlayer = null;
    }
    private void seekVideo(String timestamp) {
        int seconds = parseTimeToSeconds(timestamp);
        mediaPlayer.seek(Duration.seconds(seconds));
    }
    public void changeSpeed(ActionEvent event) {
        Double speed = speedBox.getValue();
        if (mediaPlayer != null) {
            mediaPlayer.setRate(speed);
        }
    }

    @FXML
    // Hàm xử lý chuyển chế độ fullscreen
    public void toggleFullscreen(ActionEvent event) {
        Stage stage = (Stage) mediaView.getScene().getWindow();

        // Kiểm tra chế độ fullscreen
        if (stage.isFullScreen()) {
            mediaView.fitWidthProperty().unbind();
            mediaView.fitHeightProperty().unbind();

            mediaView.setFitWidth(770.0);
            mediaView.setFitHeight(433.125);

            // Tắt chế độ fullscreen
            stage.setFullScreen(false);
        } else {
            mediaView.fitWidthProperty().unbind();
            mediaView.fitHeightProperty().unbind();

            mediaView.setFitWidth(stage.getWidth());
            mediaView.setFitHeight(stage.getHeight());

            // Bật chế độ fullscreen
            stage.setFullScreen(true);
        }
    }


    public void editNote() {
        if (noteArea.isEditable()) {
            noteArea.setText(noteArea.getText());
            video.setNote(noteArea.getText());
            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getId().equals(video.getId())) {
                    videos.set(i, video);
                    break;
                }
            }
            VideoStorage.saveVideos(videos);
            noteArea.setEditable(false);
            editButtonNote.setText("Sửa");
        } else {
            noteArea.setEditable(true);
            editButtonNote.setText("Lưu");
        }
    }
    @FXML
    private VBox timestampContainer; // Chứa các hàng
    public void addDefaultTimestamp() {

        Duration currentTime = mediaPlayer.getCurrentTime();
        String formattedTime = formatDuration(currentTime);

        String id = UUID.randomUUID().toString();

        Timestamp newTimestamp = new Timestamp(id, formattedTime, "");
        timestamps.add(newTimestamp);

        addTimestampRow(newTimestamp);
    }

    public void addTimestampRow(Timestamp timestamp) {
        HBox row = new HBox();
        row.setSpacing(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setUserData(timestamp.getId());

        StackPane timestampPane = new StackPane();
        timestampPane.setPrefWidth(100);
        TextField timestampField = new TextField(timestamp.getTimestamp());
        timestampField.setPrefWidth(100);
        timestampField.setAlignment(Pos.CENTER);

        StackPane notePane = new StackPane();
        notePane.setPrefWidth(500);
        TextField noteField = new TextField(timestamp.getDescription());
        noteField.setPrefWidth(500);

        Label timestampLabel = new Label(timestamp.getTimestamp());
        Label noteLabel = new Label(timestamp.getDescription());

        // ✅ Khi load UI, hiển thị dạng Label
        timestampLabel.setVisible(true);
        noteLabel.setVisible(true);
        timestampField.setVisible(false);
        noteField.setVisible(false);

        noteLabel.setWrapText(true);
        timestampLabel.getStyleClass().add("timestamp-label");
        noteLabel.getStyleClass().add("note-label");

        // Nút Lưu/Sửa
        Button saveEditButton = new Button("Sửa"); // 🔹 Mặc định là "Sửa"
        saveEditButton.setOnAction(e -> {
            if (saveEditButton.getText().equals("Lưu")) {
                // Chuyển về trạng thái Label
                timestampLabel.setText(timestampField.getText());
                noteLabel.setText(noteField.getText());
                timestampLabel.setVisible(true);
                noteLabel.setVisible(true);
                timestampField.setVisible(false);
                noteField.setVisible(false);
                saveEditButton.setText("Sửa");

                // Cập nhật dữ liệu
                updateTimestampInList(timestamp.getId(), timestampField.getText(), noteField.getText());
                saveTimestampsToVideo();
            } else {
                // Chuyển sang trạng thái chỉnh sửa
                timestampField.setVisible(true);
                noteField.setVisible(true);
                timestampLabel.setVisible(false);
                noteLabel.setVisible(false);
                saveEditButton.setText("Lưu");
            }
        });

        // Nút Xóa
        Button deleteButton = new Button("Xóa");
        deleteButton.setOnAction(e -> {
            timestampContainer.getChildren().remove(row);
            removeTimestampFromList(timestamp.getId());
            saveTimestampsToVideo();
        });

        // Tua video khi nhấn vào timestamp
        timestampLabel.setOnMouseClicked(ev -> seekVideo(timestamp.getTimestamp()));

        timestampPane.getChildren().addAll(timestampLabel, timestampField);
        notePane.getChildren().addAll(noteLabel, noteField);
        row.getChildren().addAll(timestampPane, notePane, saveEditButton, deleteButton);

        timestampContainer.getChildren().add(row);
    }

    private void updateTimestampInList(String id, String timestamp, String description) {
        for (Timestamp ts : timestamps) {
            if (ts.getId().equals(id)) {
                ts.setTimestamp(timestamp);
                ts.setDescription(description);
                break;
            }
        }
    }
    private void removeTimestampFromList(String id) {
        timestamps.removeIf(ts -> ts.getId().equals(id));
    }
    private void loadUiFromVideo() {
        timestampContainer.getChildren().clear();
        for (Timestamp ts : timestamps) {
            addTimestampRow(ts);
        }
        noteArea.setText(video.getNote());
    }
    private void saveTimestampsToVideo() {
        video.setTimestampList(timestamps);
        for (int i = 0; i < videos.size(); i++) {
            if (videos.get(i).getId().equals(video.getId())) {
                videos.set(i, video);
                break;
            }
        }
        System.out.println(videos);
        VideoStorage.saveVideos(videos);
    }
    private String formatDuration(Duration duration) {
        long seconds = (long) duration.toSeconds();
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }
    private int parseTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = 0, minutes = 0, seconds = 0;
        if (parts.length == 3) {
            hours = Integer.parseInt(parts[0]);
            minutes = Integer.parseInt(parts[1]);
            seconds = Integer.parseInt(parts[2]);
        } else if (parts.length == 2) {
            minutes = Integer.parseInt(parts[0]);
            seconds = Integer.parseInt(parts[1]);
        }
        return hours * 3600 + minutes * 60 + seconds;
    }


}