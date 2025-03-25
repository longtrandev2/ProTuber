package org.example.protuber.model;

public class Timestamp {
    private String id;
    private  String timestamp;
    private  String description;

    public Timestamp(String id, String timestamp, String description) {
        this.timestamp = timestamp;
        this.description = description;
        this.id = id;
    }
    public Timestamp() {
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return "Timestamp{" +
                "id='" + id + '\'' +
                "time=" + timestamp +  // Thay 'time' bằng thuộc tính của bạn
                ", note='" + description + '\'' + // Nếu có trường 'note'
                '}';
    }
}
