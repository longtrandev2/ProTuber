package org.example.protuber.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MediaValidator {
    public static boolean isValidUrl(String path) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try {
                    System.out.println("Kiem tra file");
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }
            return false;
    }
    public static boolean isLocalUrl(String videoUrl) {
        if (videoUrl.startsWith("http") || videoUrl.startsWith("https")) {
            return false;
        }
        return true;
    }

        private static boolean canPlayMedia(String mediaPath) {
            try {

                Media media = new Media(new File(mediaPath).toURI().toString()); // Đảm bảo đường dẫn hợp lệ
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                return true;
            } catch (MediaException e) {
                return false;
            }
        }
}
