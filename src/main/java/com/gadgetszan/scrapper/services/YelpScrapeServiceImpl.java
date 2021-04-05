package com.gadgetszan.scrapper.services;

import com.gadgetszan.scrapper.model.YelpReview;
import com.gadgetszan.scrapper.utils.Scrapper;
import com.gadgetszan.scrapper.utils.SeleniumScrapping;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class YelpScrapeServiceImpl implements YelpScrapeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(YelpScrapeServiceImpl.class);

    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    Scrapper scrapper = new Scrapper();
    SeleniumScrapping seleniumScrapping = new SeleniumScrapping();
    String maxPage = "";

    @Override
    public List<YelpReview> getReviews(String url) throws Exception {
        List<YelpReview> yelpReviews = seleniumScrapping.getReviews(url);
        yelpReviews.forEach((review) -> {
            review
                    .setVisionImageLabels(visionImageLabels(review.getAvatar()));
        });
        return yelpReviews;
    }

    @Override
    public List<YelpReview> getAllReviews(String url) throws Exception{
        LOGGER.info(">>>Start searching for all reviews for all pages<<<");
        Integer maxPage = seleniumScrapping.getMaxPage(url);
        LOGGER.info("Max page: " + maxPage);
        List<String> allUrls = seleniumScrapping.getUrls(url,maxPage);
        LOGGER.info("All urls: " + allUrls.toString());
        List<YelpReview> allReviews = new ArrayList();

        for(String urls:allUrls){
           List<YelpReview> yelpReviews = seleniumScrapping.getReviews(urls);
           allReviews = Stream
                        .concat(yelpReviews.stream(),allReviews.stream())
                        .collect(Collectors.toList());
           LOGGER.info("Merging All Reviews for url: " + urls);
        }

        allReviews.forEach((review) -> {
            review
                    .setVisionImageLabels(visionImageLabels(review.getAvatar()));
            LOGGER.info("Fetching Google Vision Detials" + review.getAvatar());
        });
        LOGGER.info("All Reviews: " + allReviews.size());
        return allReviews;
    };


    private Map<String,Float> visionImageLabels(String url) {
        AnnotateImageResponse response
                = cloudVisionTemplate.analyzeImage(
                resourceLoader
                        .getResource(url)
                , Feature.Type.LABEL_DETECTION);

        Map<String, Float> imageLabels =
                response
                        .getLabelAnnotationsList()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        EntityAnnotation::getDescription,
                                        EntityAnnotation::getScore,
                                        (u,v) ->{
                                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                                        },
                                        LinkedHashMap::new));

        return imageLabels;
    };
}
