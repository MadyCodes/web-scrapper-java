package com.example.webscrapingfl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

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
public class WebscrapingflApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebscrapingflApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // Initialize WebDriver and ExecutorService
            System.setProperty("webdriver.chrome.driver", // add your chromedriver path here if you are executing it as
                                                          // it is based on my pc
                    "/Users/vishnumadhav/Desktop/chromedriver-mac-arm64-final/chromedriver");
            WebDriver driver = new ChromeDriver();
            ExecutorService executor = Executors.newFixedThreadPool(5);

            ScrapeData scrape = new ScrapeData();

            ScrapeTVShows scrap = new ScrapeTVShows();

            scrap.scrapeShows(driver, "https://elcinema.com/en/index/work/category/3/",
                    "tvShow");

            // Scrape data

            scrape.scrapeData(driver, "https://elcinema.com/en/now/", "movie");

            // Quit WebDriver
            driver.quit();
        };
    }
}