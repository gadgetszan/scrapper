package com.gadgetszan.scrapper.utils;

import com.gadgetszan.scrapper.model.YelpReview;
import com.google.api.client.util.Lists;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SeleniumScrapping {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumScrapping.class);

    public List<YelpReview> getReviews(String url) throws Exception{
        //*-should include absolute path - should be in properties
        System.setProperty("webdriver.chrome.driver","D:\\JAVA\\scrapper\\chromedriver\\chromedriver.exe");
        //should be created as bean
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        driver.get(url);
        List<WebElement> reviews =
                driver.findElements(By.xpath("//section[@aria-label='Recommended Reviews']/div/div/ul/li"));
        System.out.println(reviews.size());

        List<YelpReview> yelpReviews = new ArrayList<>();
        String xPathBase = "//section[@aria-label='Recommended Reviews']/div/div/ul/li[";

        for (int i =0;i<reviews.size();i++){
            String xPathAuthor = xPathBase + (i+1) + "]//span/a";
            String xPathLocation =xPathBase + (i+1) + "]/div/div/div/div/div/div/div[2]/div/div/span";
            String xPathDate = xPathBase + (i+1) + "]/div/div[2]/div/div[2]/span";
            String xPathRating = xPathBase + (i+1) + "]/div/div[2]/div/div/span/div";
            String xPathDescription = xPathBase + (i+1) + "]//p/span" ;
            String xPathAvatar = xPathBase + (i+1) + "]//img";

            YelpReview yelpReview = new YelpReview();
            yelpReview.setAuthor(driver.findElement(By.xpath(xPathAuthor)).getText());
            yelpReview.setLocation(driver.findElement(By.xpath(xPathLocation)).getText());
            yelpReview.setDatePublished(driver.findElement(By.xpath(xPathDate)).getText());
            String rating = driver.findElement(By.xpath(xPathRating)).getAttribute("aria-label");
            yelpReview.setRatingValue(Integer.parseInt(String.valueOf(rating.charAt(0))));
            yelpReview.setDescription(driver.findElement(By.xpath(xPathDescription)).getText());
            yelpReview.setAvatar(driver.findElement(By.xpath(xPathAvatar)).getAttribute("src"));
            yelpReviews.add(yelpReview);
        }
        driver.close();
        return yelpReviews;
    }

    public String getMaxPage(String url){
        System.setProperty("webdriver.chrome.driver","D:\\JAVA\\scrapper\\chromedriver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        driver.get("https://www.yelp.com/biz/my-kitchen-by-chef-chris-manila");

        List<WebElement> lastPage =
                driver.findElements(By.xpath("//div[starts-with(@class,' pagination-link-container')]"));

        String xpathString = "//div[starts-with(@class," +
                "' pagination-link-container')]["+ lastPage.size() +"]//div";

        System.out.println(xpathString);
        String maxPage = driver.findElement
                (By.xpath(xpathString)).getText();


        //System.out.println(maxPage);
        driver.close();
        return "";
    }

    public List<YelpReview> setGoogleVision(List<YelpReview> yelpReviews) throws Exception,IOException {
        ImageAnnotatorClient vision = ImageAnnotatorClient.create();

        String fileName =  "https://s3-media0.fl.yelpcdn.com/photo/hN4x39SFEo7modlDU9kQVw/60s.jpg";

        Path path = Paths.get(fileName);
        byte[] data = Files.readAllBytes(path);
        ByteString imgBytes = ByteString.copyFrom(data);

        // Builds the image annotation request
        List<AnnotateImageRequest> requests = new ArrayList<>();
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        // Performs label detection on the image file
        BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
        List<AnnotateImageResponse> responses = response.getResponsesList();

        for (AnnotateImageResponse res : responses) {
            if (res.hasError()) {
                System.out.format("Error: %s%n", res.getError().getMessage());
                return null;
            }

            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                annotation
                        .getAllFields()
                        .forEach((k, v) -> System.out.format("%s : %s%n", k, v.toString()));
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

    }
}
