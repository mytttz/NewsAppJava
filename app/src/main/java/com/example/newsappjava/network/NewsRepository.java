package com.example.newsappjava.network;

import retrofit2.Call;

public class NewsRepository {
    private final ApiService apiService;

    public NewsRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public Call<NewsResponse> getNews() {
        return apiService.getNews(1, "bbc.com", "ru", ApiService.APIKEY);
    }
}