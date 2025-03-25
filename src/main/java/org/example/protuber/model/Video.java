package org.example.protuber.model;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Video {
    private String id;
    private String title;
    private String localUrl;
    private String originalUrl;
    private String tag;
    private String note ;
    private List<Timestamp> timestampList;
    @JsonCreator
    public Video(@JsonProperty("id") String id,
                 @JsonProperty("title") String title,
                 @JsonProperty("localUrl") String localUrl,
                 @JsonProperty("originalUrl") String originalUrl,
                 @JsonProperty("tag") String tag,
                 @JsonProperty("note") String note,
                @JsonProperty("timestampList") List<Timestamp> timestampList)
    {
        this.id = id;
        this.title = title;
        this.localUrl = localUrl;
        this.originalUrl = originalUrl;
        this.tag = (tag != null) ? tag : "None";
        this.note= (note != null) ? note : "";
        this.timestampList = (timestampList != null) ? timestampList : new ArrayList<>();
    }
    public Video(
                 @JsonProperty("localUrl") String localUrl,
                 @JsonProperty("originalUrl") String originalURl
        ) {
        this.localUrl = localUrl;
        this.originalUrl = originalURl;
    }

    public String getId() { return id; }
    public String getTitle() { return title;}
    public String getLocalUrl() { return localUrl; }
    public String getOriginalUrl() { return originalUrl; }
    public String getTag() { return tag; }
    public String getNote() { return note; }
    public List<Timestamp> getTimestampList() { return timestampList;
    }


    public void setNote(String note) {
        this.note = note;
    }

    public void setId(String id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setTag(String tag) { this.tag = tag;
    }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
    public void setTimestampList(List<Timestamp> timestampList) { this.timestampList = timestampList;}

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", localUrl='" + localUrl + '\'' +
                ", tag=" + tag + '\'' +
                ", note=" + note +  '\'' +
                ", timestampList=" + timestampList+"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Video video = (Video) obj;
        return Objects.equals(id, video.id);
    }
}
