package org.example.protuber.service;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import org.example.protuber.controller.HomeViewController;
import org.example.protuber.controller.VideoItemController;
import org.example.protuber.model.Video;
import org.example.protuber.storage.VideoStorage;
import org.example.protuber.utils.*;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VideoService implements Initializable {
    private static List<Video> videos = new ArrayList<>();
    private HomeViewController homeViewController;
    private static VideoService instance;

    public VideoService() {
        videos = VideoStorage.loadVideos();
    }

    public VideoService(HomeViewController homeViewController) {
        this.homeViewController = homeViewController;
        videos = VideoStorage.loadVideos();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public static VideoService getInstance() {
        if (instance == null) {
            instance = new VideoService();
        }
        return instance;
    }

    public boolean addVideo(String localUrl, String originalUrl) {
        if (!MediaValidator.isValidUrl(localUrl)) {
            System.out.println("Invalid URL");
            return false;
        }
        if(hasVideo(originalUrl)) {
            return false;
        }
        Video video = new Video(localUrl,originalUrl );
        video.setId(Integer.toString(videos.size() + 1));
        video.setTitle("Video " + video.getId());
        videos.add(video);
        System.out.println(videos);
        // ⚡ Cập nhật giao diện phải chạy trên JavaFX Application Thread
        Platform.runLater(() -> homeViewController.addVideoToUI(video));
        VideoStorage.saveVideos(videos);
        return true;
    }

    public void removeVideo(Video video) {
        boolean removed = videos.remove(video);
        try {
            Files.delete(Paths.get(video.getLocalUrl()));
            System.out.println("File deleted successfully.");
        } catch (Exception e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }
        VideoStorage.saveVideos(videos);
    }
    public static boolean hasVideo(String originalUrl) {
        videos = VideoStorage.loadVideos();
        if (videos.stream().anyMatch(v -> v.getOriginalUrl().equals(originalUrl))) {
            return true;
        }
        return false;
    }
    public static Video getVideoById(List<Video> videos, String id) {
        for (Video video : videos) {
            if (video.getId().equals(id)) {
                return video;
            }
        }
        return null;
    }

    public static Video getVideoByUrl(String url) {
        videos = VideoStorage.loadVideos();
        for (Video video : videos) {
            if (video.getLocalUrl().equals(url)) {
                return video;
            }
        }
        return null;
    }

    public List<Video> getVideos() {
        return videos;
    }

}