package com.example.webscrapingfl;

import org.springframework.boot.SpringApplication;
import io.github.cdimascio.dotenv.Dotenv;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.example.webscrapingfl.scrapper.ScrapeData;
// import com.example.webscrapingfl.scrapper.ScrapeData;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.webscrapingfl") // Ensure that Spring scans the components
public class WebscrapingflApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebscrapingflApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // Initialize WebDriver and ExecutorService
            System.setProperty("webdriver.chrome.driver",
                    "/Users/vishnumadhav/Desktop/chromedriver-mac-arm64-final/chromedriver");
            WebDriver driver = new ChromeDriver();
            ExecutorService executor = Executors.newFixedThreadPool(5);

            ScrapeData scrape = new ScrapeData();
            ScrapeTVShows scrap = new ScrapeTVShows();

            // Scrape data
            scrap.scrapeShows(driver, "https://elcinema.com/en/index/work/category/3/", "tvShow");

            scrape.scrapeData(driver, "https://elcinema.com/en/now/", "movie");

            // Quit WebDriver
            driver.quit();
        };
    }

    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure()
                .directory("/Users/vishnumadhav/Desktop/web-scrapper-selenium/.env")
                .load();
    }

    @Bean
    public BasicAWSCredentials awsCredentials(Dotenv dotenv) {
        return new BasicAWSCredentials(
                dotenv.get("AWS_ACCESS_KEY_ID"),
                dotenv.get("AWS_SECRET_ACCESS_KEY"));
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB(BasicAWSCredentials awsCredentials) {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion("us-east-1")
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}