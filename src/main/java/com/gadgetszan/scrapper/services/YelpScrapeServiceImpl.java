package com.gadgetszan.scrapper.services;

import com.gadgetszan.scrapper.model.YelpReview;
import com.gadgetszan.scrapper.utils.Scrapper;
import com.gadgetszan.scrapper.utils.SeleniumScrapping;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YelpScrapeServiceImpl implements YelpScrapeService {

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
        yelpReviews.forEach((review) ->{
            review
                    .setVisionImageLabels(visionImageLabels(review.getAvatar()));

        });
        return yelpReviews;
    }


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
