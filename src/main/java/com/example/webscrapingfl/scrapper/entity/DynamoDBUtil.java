package com.example.webscrapingfl.scrapper.entity;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
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
import java.util.Set;
import java.util.Objects;

import java.util.HashSet;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DynamoDBUtil {

    public void saveMoviesToDynamoDB(List<Movie> movies) {
        try {
            BasicAWSCredentials credentials = new BasicAWSCredentials("AKIA6MA3ZTWLVGDRS6PM",
                    "FcMc74W7c1W2N5+XoyAm+HjQXsvlJ51qJf2oSJ40");
            AmazonDynamoDB dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
                    .withRegion("us-east-1")
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
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
                                "elcinema_id", new AttributeValue(UUID.randomUUID().toString()), // Include elcinema_id
                                                                                                 // attribute
                                "id", new AttributeValue(UUID.randomUUID().toString()),
                                "title", new AttributeValue(getNonNullValue(movie.getTitle())),
                                "description", new AttributeValue(getNonNullValue(movie.getDescription())),
                                "genre", new AttributeValue().withSS(getNonEmptyStringSet(movie.getGenres())),
                                "cast", new AttributeValue().withSS(getNonEmptyStringSet(movie.getCasts()))));

                // Use
                // getNonEmptyStringSet
                // "director", new AttributeValue(getNonNullValue(movie.getDirector()))));
                dynamoDbClient.putItem(request);

            }
            System.out.println("Movies Stored in DynamoDB");
        } catch (AmazonServiceException e) {
            System.err.println(e);
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
    }

    // Helper method to return an empty string if the input is null
    private String getNonNullValue(Object value) {
        return value != null ? value.toString() : "";
    }

    private Set<String> getNonEmptyStringSet(List<?> list) {
        // Filter out null and empty strings
        return list != null
                ? list.stream()
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet())
                : null;
    }

}
