package com.gadgetszan.scrapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YelpReview {
    private String author;
    private String location;
    private String datePublished;
    private Integer ratingValue;
    private String description;
    private String avatar;
    private String googleApi;
    private Map<String, Float> visionImageLabels;
}
