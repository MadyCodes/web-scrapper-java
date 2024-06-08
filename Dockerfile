# Use a Java 11 base image
FROM eclipse-temurin:11

# Set the working directory to /app
WORKDIR /app

# Copy the Maven dependencies
COPY pom.xml .

RUN apt-get update && apt-get install -y maven

# Create a Maven project with a pom.xml file
WORKDIR /app
RUN mkdir -p /app/src/main/java

# Create a valid pom.xml file
RUN echo '<project><modelVersion>4.0.0</modelVersion><groupId>com.example</groupId><artifactId>my-app</artifactId><version>1.0</version><build><plugins><plugin><groupId>org.apache.maven.plugins</groupId><artifactId>maven-compiler-plugin</artifactId><version>3.10.1</version><configuration><source>1.8</source><target>1.8</target></configuration></plugin></plugins></build></project>' > /app/pom.xml

# Compile the Maven project
RUN mvn compile

ENV M2_HOME=/usr/lib/maven
ENV MAVEN_CONFIG=/usr/lib/maven/conf



# Copy the application code
COPY src/main/java /app/src/main/java
COPY src/main/resources /app/src/main/resources

# Compile the application
RUN mvn package -DskipTests -Djava.version=11

# Create a jar file
RUN mvn package -DskipTests -Djava.version=11

# Install Chrome and ChromeDriver
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN dpkg -i google-chrome-stable_current_amd64.deb
RUN rm google-chrome-stable_current_amd64.deb

RUN wget https://chromedriver.storage.googleapis.com/2.46/chromedriver_linux64.zip
RUN unzip chromedriver_linux64.zip
RUN mv chromedriver /usr/local/bin/chromedriver
RUN chmod +x /usr/local/bin/chromedriver
RUN rm chromedriver_linux64.zip

# Set environment variables for AWS credentials
ENV AWS_ACCESS_KEY_ID=<YOUR_AWS_ACCESS_KEY_ID>
ENV AWS_SECRET_ACCESS_KEY=<YOUR_AWS_SECRET_ACCESS_KEY>
ENV AWS_REGION=<YOUR_AWS_REGION>

# Set environment variable for ChromeDriver
ENV PATH=$PATH:/usr/local/bin/chromedriver

# Set the environment variable for the JVM arguments
ENV JVM_ARGS="--add-opens java.base/java.lang=ALL-UNNAMED -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

# Expose the port
EXPOSE 8080

# Run the application with the JVM arguments
CMD ["java", "-jar", "target/webscrapingfl-0.0.1-SNAPSHOT.jar", "-Dspring-boot.run.jvmArguments=${JVM_ARGS}"]