package com.example.webscrapingfl.scrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
// import java.util.Map;
// import java.util.UUID;
// import java.util.stream.Collectors;

import com.example.webscrapingfl.scrapper.entity.Movie;
import com.example.webscrapingfl.scrapper.entity.TVShow;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

public class DatabaseUtil {

    public void saveMoviesToDatabase(List<Movie> movies) {
        String dbUrl = "jdbc:mysql://pa-datasources-cluster.cluster-ctkeuweqhlpx.us-east-1.rds.amazonaws.com:3306/assessment";
        String username = "madhav";
        String password = "Madhav@Parr0t!";
        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            Statement statement = connection.createStatement();
            String createTableQuerymovie = "CREATE TABLE IF NOT EXISTS movieDetails (" +
                    "id INT AUTO_INCREMENT, " +
                    "title VARCHAR(255), " +
                    "release_date VARCHAR(255), " +
                    // "description TEXT, " +
                    // "genre VARCHAR(255), " +
                    // "cast VARCHAR(255), " +
                    "PRIMARY KEY (id)" +
                    ")";
            statement.executeUpdate(createTableQuerymovie);
            try (PreparedStatement pstmt = conn
                    .prepareStatement(
                            "INSERT INTO movieDetails (title, release_date) VALUES (?,?)")) {
                for (Movie movie : movies) {
                    pstmt.setString(1, movie.getTitle());
                    pstmt.setString(2, movie.getReleaseDate());
                    // pstmt.setString(3, movie.getDescription());
                    // pstmt.setString(4, movie.getGenres().toString());
                    // pstmt.setString(5, movie.getCasts().toString());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                System.out.println("Movie Title and release date stored in SQL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try (PrintWriter writer = new PrintWriter(new FileWriter("output__sql_movies" + ".csv"))) {
                writer.println("Title,Release Date, Description, Genre, Cast");
                for (Movie movie : movies) {
                    writer.println(movie.getTitle() + "," + movie.getReleaseDate());
                    // + "," + movie.getDescription() + ","
                    // + movie.getGenres() + "," + movie.getCasts());
                }
                System.out.println("Output written to output__sql_movies" + ".csv");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveTVShowsToDatabase(List<TVShow> tvShow) {
        String dbUrl = "jdbc:mysql://pa-datasources-cluster.cluster-ctkeuweqhlpx.us-east-1.rds.amazonaws.com:3306/assessment";
        String username = "madhav";
        String password = "Madhav@Parr0t!";
        String tableName;

        // if (type.equals("movie")) {
        tableName = "TVShow";
        // }
        // else {
        // tableName = "tvShows";
        // }

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            Statement statement = connection.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS tvshowDetails (" +
                    "id INT AUTO_INCREMENT, " +
                    "name VARCHAR(255), " +
                    "date VARCHAR(255), " +
                    // "description TEXT, " +
                    // "genre VARCHAR(255), " +
                    // "cast VARCHAR(255), " +
                    "PRIMARY KEY (id)" +
                    ")";

            statement.executeUpdate(createTableQuery);

            try (PreparedStatement pstmt = conn
                    .prepareStatement(
                            "INSERT INTO tvshowDetails (name, date) VALUES (?,?)")) {
                for (TVShow show : tvShow) {
                    pstmt.setString(1, show.getName());
                    pstmt.setString(2, show.getDate());
                    // pstmt.setString(3, show.getRating());
                    // pstmt.setString(4, show.getName());
                    // pstmt.setString(5, show.getName());
                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                System.out.println("TV Show Name and release year stored in SQL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try (PrintWriter writer = new PrintWriter(new FileWriter("output_sql_TVSHOW" + ".csv"))) {
                writer.println("Title,Release Date");
                for (TVShow show : tvShow) {
                    writer.println(show.getName() + "," + show.getDate() + "," + show.getRating());
                }
                System.out.println("Output written to output_sql_TVSHOW" + ".csv");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}