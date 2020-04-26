package com.hiepdt.annavoochackathon.models;

public class Messenger {
    private String createBy;
    private String content;
    private long timestamp;
    private String url;

    public Messenger() {
    }

    public Messenger(String createBy, String content, long timestamp, String url) {
        this.createBy = createBy;
        this.content = content;
        this.timestamp = timestamp;
        this.url = url;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
