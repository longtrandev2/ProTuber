package org.example.protuber.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.protuber.model.Video;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoStorage {
    private static final String FILE_PATH = "./videos-json/videos.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void saveVideos(List <Video> videos)  {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), videos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Video> loadVideos() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new java.util.ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Video>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}

