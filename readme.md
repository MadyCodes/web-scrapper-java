# Web Scraping Application

This Java application scrapes movie data from a webpage using Selenium WebDriver. The scraped data is then saved into a database and DynamoDB.

## Project Structure

- `ScrapeData.java`: Contains the main scraping logic.
- `Movie.java`: Entity class representing a movie.
- `DynamoDBUtil.java`: Utility class for saving data to DynamoDB.
- `DatabaseUtil.java`: Utility class for saving data to a database.

## Prerequisites

- Java Development Kit (JDK) 8 or later
- Selenium WebDriver
- WebDriver executable (e.g., ChromeDriver for Chrome)
- AWS SDK for Java (for DynamoDB integration)
- Database driver (e.g., JDBC for SQL database)

## Setup

1. **Install Java and Selenium WebDriver:**

   - Ensure JDK is installed on your machine.
   - Add Selenium WebDriver dependency to your `pom.xml` or include the JARs in your project.

2. **Download WebDriver:**

   - Download the appropriate WebDriver executable (e.g., ChromeDriver) and add it to your system PATH.

3. **AWS SDK and Database Driver:**
   - Add dependencies for AWS SDK and your database driver to your `pom.xml`.

## Usage

1. **Initialize WebDriver:**

   - Create and configure a WebDriver instance (e.g., ChromeDriver).

2. **Scrape Data:**
   - Instantiate the `ScrapeData` class.
   - Call the `scrapeData` method with the WebDriver instance, target URL, and type of data.

## Command to run

- mvn spring-boot:run -Dspring-boot.run.jvmArguments="--add-opens java.base/java.lang=ALL-UNNAMED"
