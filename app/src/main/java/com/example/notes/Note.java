package com.example.notes;

import com.example.notes.util.DateUtil;

public class Note {
    private int id;
    private String title;
    private String content;
    private long timestamp; // Ändere den Typ auf long
    private int categoryId;

    public Note(int id, String title, String content, long timestamp, int categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTimestampFromatted() {
        return DateUtil.formatTimestamp(String.valueOf(timestamp)); // Formatierung bleibt gleich
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
