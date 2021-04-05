package com.gadgetszan.scrapper.controllers;

import com.gadgetszan.scrapper.model.YelpReview;
import com.gadgetszan.scrapper.model.YelpScrapeRequest;
import com.gadgetszan.scrapper.services.YelpScrapeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/yelp/scrape")
public class YelpScrapperController {
    @Autowired
    YelpScrapeService yelpScrapeService;

    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/reviews")
    public List<YelpReview>
            getScrapeReviewsPerPage(@RequestBody YelpScrapeRequest scrapeRequest) throws Exception{

        List<YelpReview> yelpReviews =
                yelpScrapeService.getReviews(scrapeRequest.getUrl());
        return yelpReviews;
    }


    @PostMapping("/allReviews")
    public List<YelpReview>
        getScrapeAllReviewsAllPage(@RequestBody YelpScrapeRequest scrapeRequest) throws Exception{
        List<YelpReview> yelpReviews =
                yelpScrapeService.getAllReviews(scrapeRequest.getUrl());
        return yelpReviews;
    }

}
