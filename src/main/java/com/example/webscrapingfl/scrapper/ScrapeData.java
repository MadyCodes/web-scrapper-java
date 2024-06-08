package com.example.webscrapingfl.scrapper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.webscrapingfl.scrapper.entity.DynamoDBUtil;
import com.example.webscrapingfl.scrapper.entity.Movie;

import java.util.ArrayList;
import java.util.List;

public class ScrapeData {

    public void scrapeData(WebDriver driver, String url, String type) {
        driver.get(url);
        System.out.println("Page loaded: " + url);

        List<Movie> movies = new ArrayList<>();
        List<WebElement> rows = driver.findElements(By.cssSelector("div.row[id]"));

        for (WebElement row : rows) {
            Movie movie = new Movie();

            // Extract data from row
            WebElement titleElement = row.findElement(By.cssSelector("h3 > a"));
            movie.setTitle(titleElement.getText());

            WebElement starRatingElement = row.findElement(By.cssSelector("div.stars-rating-lg span.legend"));
            movie.setStarRating(starRatingElement.getText());

            WebElement descriptionElement = row.findElement(By.cssSelector("p"));
            movie.setDescription(descriptionElement.getText());

            WebElement releaseDateElement = new WebDriverWait(driver, 10).until(ExpectedConditions
                    .presenceOfElementLocated(By.cssSelector("ul.list-separator.list-title strong + a")));
            movie.setReleaseDate(releaseDateElement.getText());

            List<WebElement> genreElements = row.findElements(By.cssSelector("ul.list-separator.list-title li a"));
            List<String> genres = new ArrayList<>();
            for (WebElement genreElement : genreElements) {
                genres.add(genreElement.getText());
            }
            movie.setGenre(genres);

            movies.add(movie);
        }

        System.out.println("Movies scraped: " + movies.size());

        // Print movie data
        for (Movie movie : movies) {
            System.out.println("Title: " + movie.getTitle());
            System.out.println("Description: " + movie.getDescription());
            System.out.println("Release Date: " + movie.getReleaseDate());
            System.out.println("Genres: " + movie.getGenres());
            System.out.println("Rating: " + movie.getStarRating());
            System.out.println();
        }

        // Save data to database and DynamoDB
        DatabaseUtil databaseUtil = new DatabaseUtil();
        databaseUtil.saveDataToDatabase(movies, type);
        DynamoDBUtil dynamoDBUtil = new DynamoDBUtil();
        dynamoDBUtil.saveDataToDynamoDB(movies);
    }
}