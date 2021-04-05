package com.gadgetszan.scrapper.utils;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.SneakyThrows;

import java.util.List;

public class XpathTest {
    //single front slash -/html/body[1]/div[3]/div[2]
    //double front slash - // anywhere in the document
    //functions
    //contains - //img[contains(@src,'sprites')]


    public static void main(String[] args) throws Exception  {
       final String url =  "https://www.yelp.com/biz/ilustrado-restaurant-manila?osq=Restaurants";
//        final String url =  "https://www.google.com/";
//
        WebClient client = new WebClient();
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        HtmlPage page = client.getPage(url);

        List<HtmlElement> elementLists = page
                .getByXPath("//div[@class=' review__373c0__13kpL border-color--default__373c0__3-ifU']");

        System.out.println("Number of Elements: " + elementLists.size());

        elementLists.forEach((elementList)->{
            System.out.println("Element class: " + elementList.getAttribute("class"));
            System.out.println("Element aria: " + elementList.getAttribute("aria-label"));
            System.out.println(" ");
        });

    }

}
