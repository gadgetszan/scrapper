package com.gadgetszan.scrapper.services;

import com.gadgetszan.scrapper.model.YelpReview;

import java.util.List;

public interface YelpScrapeService {
    List<YelpReview> getReviews(String url) throws Exception;
}
