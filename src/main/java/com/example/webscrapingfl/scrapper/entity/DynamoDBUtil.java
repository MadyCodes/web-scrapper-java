package com.example.webscrapingfl.scrapper.entity;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.opencsv.CSVWriter;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

public class DynamoDBUtil {

    public void saveMoviesToDynamoDB(List<Movie> movies) {
        try {
            AmazonDynamoDB dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
                    .withRegion("us-east-1") // Set the region here
                    .build();

            String tableName = "madhav_assessment";

            for (Movie movie : movies) {
                if (movie.getCasts().isEmpty()) {
                    movie.setCast(Collections.singletonList("no cast"));
                }
                if (movie.getGenres().isEmpty()) {
                    movie.setGenre(Collections.singletonList("no genre"));
                }
                PutItemRequest request = new PutItemRequest()
                        .withTableName(tableName)
                        .withItem(Map.of(
                                "elcinema_id", new AttributeValue(UUID.randomUUID().toString()),
                                "id", new AttributeValue(UUID.randomUUID().toString()),
                                "title", new AttributeValue(getNonNullValue(movie.getTitle())),
                                "description", new AttributeValue(getNonNullValue(movie.getDescription())),
                                "genre", new AttributeValue().withSS(getNonEmptyStringSet(movie.getGenres())),
                                "cast", new AttributeValue().withSS(getNonEmptyStringSet(movie.getCasts()))));

                dynamoDbClient.putItem(request);
            }
            System.out.println("Movies Stored in DynamoDB");
        } catch (AmazonServiceException e) {
            System.err.println(e);
            writeMoviesToCSV(movies);
        }
    }

    private void writeMoviesToCSV(List<Movie> movies) {
        String csvFileName = "output-dynamo.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFileName))) {
            String[] header = { "id", "title", "description", "genre", "cast", "director" };
            writer.writeNext(header);

            for (Movie movie : movies) {
                String castString = movie.getCasts() != null ? String.join(",",
                        movie.getCasts().stream().map(Object::toString).collect(Collectors.toList())) : "";
                String genreString = movie.getGenres() != null ? String.join(",",
                        movie.getGenres().stream().map(Object::toString).collect(Collectors.toList())) : "";
                String[] data = new String[] {
                        UUID.randomUUID().toString(),
                        movie.getTitle(),
                        movie.getDescription(),
                        genreString,
                        castString,
                        movie.getTitle()
                };
                writer.writeNext(data);
            }
            System.out.println("Data written to CSV file: " + csvFileName);
        } catch (IOException e1) {
            System.err.println("Error writing to CSV file: " + e1.getMessage());
        }
    }

    private String getNonNullValue(Object value) {
        return value != null ? value.toString() : "";
    }

    private Set<String> getNonEmptyStringSet(List<?> list) {
        return list != null
                ? list.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet())
                : null;
    }
}