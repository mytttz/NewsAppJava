package com.example.newsappjava.network;

import java.util.List;

public class NewsResponse {

    private final List<NetworkNews> articles;

    public NewsResponse(List<NetworkNews> articles) {
        this.articles = articles;
    }

    public List<NetworkNews> getArticles() {
        return articles;
    }
}
