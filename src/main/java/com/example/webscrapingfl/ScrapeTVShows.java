package com.example.webscrapingfl;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.NoSuchElementException;

import com.example.webscrapingfl.scrapper.DatabaseUtil;
import com.example.webscrapingfl.scrapper.entity.DynamoDBUtil;
import com.example.webscrapingfl.scrapper.entity.Movie;
import com.example.webscrapingfl.scrapper.entity.TVShow;

public class ScrapeTVShows {
    public void scrapeShows(WebDriver driver, String url, String type) {
        driver.get(url);
        System.out.println("Page loaded: " + url);

        List<TVShow> tvShows = new ArrayList<>();
        List<String> releaseYears = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.cssSelector("table.expand tbody tr"));

        for (int i = 1; i < rows.size(); i++) {
            TVShow tvShow = new TVShow();
            WebElement row = rows.get(i);
            List<WebElement> anchors = row.findElements(By.cssSelector("td a"));
            String tvShowName = anchors.get(1).getText();
            tvShow.setName(tvShowName);
            WebElement releaseYearElement = row.findElement(By.cssSelector("td:nth-child(5)"));
            String releaseYear = releaseYearElement.getText();
            tvShow.setDate(releaseYear);
            // Extract TV show rating
            WebElement ratingElement = row.findElement(By.cssSelector("td div.stars-rating-lg[title]"));
            String ratingText = ratingElement.getAttribute("title");
            String ratingValue = ratingText.replaceAll("[^0-9\\.]+", ""); // extract only the numeric value
            tvShow.setRating(ratingValue);

            tvShows.add(tvShow);
        }

        System.out.println("TV Shows scraped: " + tvShows.size());

        // Print TV show data
        for (TVShow tvShow : tvShows) {
            System.out.println("Name: " + tvShow.getName());
            System.out.println("Date: " + tvShow.getDate());
            System.out.println("Rating: " + tvShow.getRating());
            System.out.println();
        }

        DatabaseUtil databaseUtil = new DatabaseUtil();
        databaseUtil.saveTVShowsToDatabase(tvShows);

        DynamoDBUtil dynamoDBUtil = new DynamoDBUtil();
        dynamoDBUtil.saveTVShowsToDynamoDB(tvShows);
    }
}
