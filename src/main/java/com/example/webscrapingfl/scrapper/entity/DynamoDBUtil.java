package com.example.webscrapingfl.scrapper.entity;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.AmazonServiceException;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DynamoDBUtil {

    public void saveDataToDynamoDB(List<Movie> movies) {
        try {
            AmazonDynamoDB dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
                    .withRegion("us-east-1") // Set the region here
                    .build();

            String tableName = "madhav_assessment";

            for (Movie movie : movies) {
                PutItemRequest request = new PutItemRequest()
                        .withTableName(tableName)
                        .withItem(Map.of(
                                "id", new AttributeValue(UUID.randomUUID().toString()),
                                "title", new AttributeValue(getNonNullValue(movie.getTitle())),
                                "description", new AttributeValue(getNonNullValue(movie.getDescription())),
                                "genre", new AttributeValue(getNonNullValue(movie.getGenres())),
                                "cast", new AttributeValue(getNonNullValue(movie.getCast())),
                                "director", new AttributeValue(getNonNullValue(movie.getDirector()))));
                dynamoDbClient.putItem(request);
            }

        } catch (AmazonServiceException e) {
            System.err.println(e);
            String csvFileName = "output-dynamo.csv";
            try (CSVWriter writer = new CSVWriter(new FileWriter(csvFileName))) {
                String[] header = { "id", "title", "description", "genre", "cast", "director" };
                writer.writeNext(header);

                for (Movie movie : movies) {
                    String[] data = new String[] {
                            UUID.randomUUID().toString(),
                            movie.getTitle(),
                            movie.getTitle(),
                            String.join(",",
                                    movie.getGenres().stream().map(Object::toString).collect(Collectors.toList())),
                            movie.getTitle(),
                            movie.getTitle()
                    };
                    writer.writeNext(data);
                }
                System.out.println("Data written to CSV file: " + csvFileName);
            } catch (IOException e1) {
                System.err.println("Error writing to CSV file: " + e1.getMessage());
            }
        }

    }

    // Helper method to return an empty list if the input is null
    private List<String> getNonNullValue(List<?> list) {
        return list != null ? list.stream().map(Object::toString).collect(Collectors.toList())
                : Collections.emptyList();
    }

    // Helper method to return an empty string if the input is null
    private String getNonNullValue(Object value) {
        return value != null ? value.toString() : "";
    }
}