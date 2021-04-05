package com.gadgetszan.scrapper.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gadgetszan.scrapper.model.YelpReview;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Scrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(Scrapper.class);
    WebClient client = new WebClient();

    public List<YelpReview> getReviews(String url) throws Exception{
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        HtmlPage page = client.getPage(url);

        HtmlElement elementList =
                page.getFirstByXPath("//script[@type='application/ld+json']");
        String jsonString = elementList.getTextContent();
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map =mapper.readValue(jsonString,
                new TypeReference<Map<String,Object>>(){});

        List<LinkedHashMap> yelpReviewsMap = (List<LinkedHashMap>) map.get("review");
        List<YelpReview> yelpReviews = new ArrayList<>();

        yelpReviewsMap.forEach((yelpReview) ->{
            YelpReview yelpReviewObj = new YelpReview();
            yelpReviewObj.setAuthor( yelpReview.get("author").toString());
            yelpReviewObj.setDatePublished(yelpReview.get("datePublished").toString());
            LinkedHashMap reviewRatingMap = (LinkedHashMap) yelpReview.get("reviewRating");
            yelpReviewObj.setRatingValue((Integer) reviewRatingMap.get("ratingValue"));
            yelpReviewObj.setDescription(yelpReview.get("description").toString());
            yelpReviews.add(yelpReviewObj);
        });

        return yelpReviews;
    }

    public static void main(String[] args) throws Exception {
        Scrapper scrapper = new Scrapper();
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        HtmlPage page = client.getPage("https://www.yelp.com/biz/sushi-yasaka-new-york");
        final String pageAsXml = page.asXml();
        System.out.println(pageAsXml);
    }
}
