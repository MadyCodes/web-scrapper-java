package com.example.webscrapingfl.scrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.webscrapingfl.scrapper.entity.Movie;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DatabaseUtil {

    public void saveDataToDatabase(List<Movie> movies, String type) {
        String dbUrl = "jdbc:mysql://pa-datasources-cluster.cluster-ctkeuweqhlpx.us-east-1.rds.amazonaws.com:3306/assessment";
        String username = "madhav";
        String password = "Madhav@Parr0t!";
        String tableName;

        if (type.equals("movie")) {
            tableName = "movies";
        } else {
            tableName = "tv_shows";
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {
            if (tableName.equals("movies")) {
                try (PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO movies (title, release_date) VALUES (?,?)")) {
                    for (Movie movie : movies) {
                        pstmt.setString(1, movie.getTitle());
                        pstmt.setString(2, movie.getReleaseDate());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                    System.out.println("Data stored in SQL");
                }
            } else {
                try (PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO tv_shows (title, release_date) VALUES (?,?)")) {
                    for (Movie movie : movies) {
                        pstmt.setString(1, movie.getTitle());
                        pstmt.setString(2, movie.getReleaseDate());
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try (PrintWriter writer = new PrintWriter(new FileWriter("output_sql" + type + ".csv"))) {
                writer.println("Title,Release Date");
                for (Movie movie : movies) {
                    writer.println(movie.getTitle() + "," + movie.getReleaseDate());
                }
                System.out.println("Output written to output_" + type + ".csv");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}