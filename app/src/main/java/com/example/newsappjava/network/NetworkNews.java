package com.example.newsappjava.network;

import com.example.newsappjava.database.DatabaseNews;

public class NetworkNews {
    private final String name;
    private final String title;
    private final String description;
    private final String publishedAt;

    public NetworkNews(String name, String title, String description, String publishedAt) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.publishedAt = publishedAt;
    }

    // Геттеры для полей

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    // Метод преобразования NetworkNews в DatabaseNews

    public DatabaseNews toDatabaseNews() {
        return new DatabaseNews(
                0,  // Устанавливаем id в 0, так как он будет автоматически сгенерирован в базе данных
                name,
                title,
                description,
                publishedAt
        );
    }
}


