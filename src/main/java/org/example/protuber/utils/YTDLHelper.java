package org.example.protuber.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YTDLHelper {
    public static final String YT_DLP_PATH = "./yt-dlp/yt-dlp.exe";
    public static final String FFMPEG_PATH = "./ffmpeg/bin/ffmpeg.exe";
    public static final String FFPROBE_PATH = "./ffmpeg/bin/ffprobe.exe";

    public interface ProgressListener {
        void onProgress(int percentage);
    }

    public static String getUniqueFileName() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "video_" + timestamp + ".mp4";
    }

    public static String getDirectVideoUrl(String videoUrl, ProgressListener listener) {
        try {
            File videoFolder = new File("Videos");
            if (!videoFolder.exists()) videoFolder.mkdirs();

            String outputPath = "Videos" + File.separator + getUniqueFileName();
        if(isValidYouTubeURL(videoUrl)) {
            videoUrl = extractVideoId(videoUrl);
        }
            ProcessBuilder builder = new ProcessBuilder(
                    YT_DLP_PATH, "-f", "bestaudio[ext=m4a]+bestvideo[ext=mp4]",
                    "--ffmpeg-location", FFMPEG_PATH,
                    "--merge-output-format", "mp4",
                    "-o", outputPath, videoUrl
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    if (line.matches(".*\\[download] +\\d+\\.\\d+%.*")) {
                        String percent = line.replaceAll(".*?(\\d+\\.\\d+)%.*", "$1");
                        try {
                            int progress = (int) Float.parseFloat(percent);
                            listener.onProgress(progress);
                        } catch (NumberFormatException ignored) {}
                    }
                    if (line.contains("[ffmpeg]") || line.contains("Merging formats into")) {
                        listener.onProgress(65);
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) return null;

            File downloadedFile = new File(outputPath);
            if (downloadedFile.exists()) {
                System.out.println("✅ Video đã tải về: " + downloadedFile.getAbsolutePath());
                if(!isH264(downloadedFile.getAbsolutePath())) {
                    listener.onProgress(70);
                    String convertedPath = convertToH264(downloadedFile.getAbsolutePath(), listener);
                    listener.onProgress(100);
                    return convertedPath;
                }
                return downloadedFile.getAbsolutePath();
            } else return null;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static int getTotalFrames(String videoPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    FFPROBE_PATH, "-v", "error", "-select_streams", "v:0", "-show_entries",
                    "stream=nb_frames", "-of", "default=nokey=1:noprint_wrappers=1", videoPath
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null) {
                    return Integer.parseInt(line.trim());
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 2000;
    }

    public static boolean isH264(String filePath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    FFMPEG_PATH, "-i", filePath
            );

            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Video: h264")) {
                        return true;
                    }
                }
            }

            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String convertToH264(String filePath , ProgressListener listener) {
        try {
            System.out.println("⚡ Bắt đầu chuyển đổi video sang H.264: " + filePath);

            // Tạo file tạm để tránh lỗi ghi đè
            String tempFilePath = filePath + "_h264.mp4";
            System.out.println("📂 File tạm: " + tempFilePath);

            ProcessBuilder builder = new ProcessBuilder(
                    FFMPEG_PATH,"-y",    "-i", filePath,
                    "-c:v", "libx264", "-preset", "ultrafast", "-crf", "23",
                    "-c:a", "copy", tempFilePath
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            int totalFrames = getTotalFrames(filePath); // Lấy số frame thực tế

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                int lastProgress = 70; // Bắt đầu từ 70%
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg: " + line);

                    // Trích xuất số frame từ log FFmpeg
                    if (line.matches(".*frame=\\s*\\d+.*")) {
                        String frameStr = line.replaceAll(".*frame=\\s*(\\d+).*", "$1");
                        try {
                            int frameCount = Integer.parseInt(frameStr);

                            // Tính phần trăm dựa trên tổng số frame thực tế
                            int progress = 70 + (frameCount * 30 / totalFrames);
                            progress = Math.min(progress, 100); // Giới hạn tối đa 100%

                            // Chỉ cập nhật nếu giá trị mới cao hơn
                            if (progress > lastProgress) {
                                listener.onProgress(progress);
                                lastProgress = progress;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Lỗi khi chuyển đổi video!");
                return filePath;
            }

            File tempFile = new File(tempFilePath);
            File originalFile = new File(filePath);

            if (tempFile.exists()) {
                System.out.println("✅ FFmpeg đã tạo xong file H.264!");
                if (originalFile.delete()) {
                    if (tempFile.renameTo(originalFile)) {
                        System.out.println("✅ Video đã được chuyển sang H.264: " + filePath);
                        return filePath;
                    } else {
                        System.out.println("❌ Không thể ghi đè file gốc, giữ lại file tạm.");
                        return tempFilePath;
                    }
                } else {
                    System.out.println("❌ Không thể xóa file gốc, giữ lại file tạm.");
                    return tempFilePath;
                }
            }

        } catch (IOException | InterruptedException e) {
            System.out.println("⚡ Lỗi khi chuyển đổi: " + e.getMessage());
            e.printStackTrace();
        }
        return filePath;
    }
    public static boolean isValidYouTubeURL(String url) {
        String youtubePattern = "^(https?://)?(www\\.)?(youtube|youtu|youtube-nocookie)\\.(com|be)/.*$";
        Pattern pattern = Pattern.compile(youtubePattern);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    public static String extractVideoId(String url) {
        String videoId = url.split("v=")[1].split("&")[0];
        return "https://www.youtube.com/watch?v=" + videoId; // Tạo lại URL video đơn
    }
}
