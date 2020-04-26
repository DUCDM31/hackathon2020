package com.hiepdt.annavoochackathon.models;

public class MesMatch {
    private String uid;
    private String chatId;
    private String content;
    private String createBy;

    public MesMatch(){}
    public MesMatch(String uid, String chatId, String content, String createBy) {
        this.uid = uid;
        this.chatId = chatId;
        this.content = content;
        this.createBy = createBy;
    }


    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
