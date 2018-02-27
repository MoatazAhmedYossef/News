package com.motaz.news.utilities;

import com.motaz.news.model.News;

import java.util.List;

public class ApiResponse {
    private String status;
    private int totalResults;
    private List<News> articles;

    public String getStatus() {
        return status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public List<News> getArticles() {
        return articles;
    }
}