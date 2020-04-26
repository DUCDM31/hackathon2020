package com.hiepdt.annavoochackathon.models;

import java.util.ArrayList;

public class Post {
    private String uid;
    private ArrayList<String> topics;
    private String status;
    private String content;
    private long timestamp;

    public Post() {

    }

    public Post(String uid, ArrayList<String> topics, String status, String content, long timestamp) {
        this.uid = uid;
        this.topics = topics;
        this.status = status;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
