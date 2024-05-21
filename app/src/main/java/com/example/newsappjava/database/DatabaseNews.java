package com.example.newsappjava.database;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "news",
        indices = {@Index(value = {"title"}, unique = true)}
)
public class DatabaseNews {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String title;
    private String description;
    private String publishedAt;

    public DatabaseNews(long id, String name, String title, String description, String publishedAt) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}