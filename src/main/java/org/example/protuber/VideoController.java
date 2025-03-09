package org.example.protuber;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VideoController implements Initializable {
    @FXML
    private MediaView mediaView;
    @FXML
    private Button playVideoButton, pauseVideoButton, resetButton;
    @FXML
    private TextField urlTextField;
    private Media media;
    private MediaPlayer mediaPlayer;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    //Play
    public void playVideo() {

        String videoUrl = urlTextField.getText().trim();

        if (videoUrl.isEmpty()) {
            System.out.println("videoUrl is empty");
            return;
        } else {
            if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
                media = new Media(videoUrl);
            } else {
                File file = new File(videoUrl);
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
        mediaPlayer.play();
    }

    //Pause và resume
    public void togglePlayPause() {
        if (mediaPlayer != null) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                pauseVideoButton.setText("Resume");
            } else if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY) {
                mediaPlayer.play();
                pauseVideoButton.setText("Pause");
            }
        }
    }

    //Reset
    public void resetVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        }
    }
}
