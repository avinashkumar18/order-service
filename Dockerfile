# Step 1: Use an official Java runtime as a base image
FROM openjdk:17-jdk-slim

# Step 2: Add a volume pointing to /tmp for use by the Spring Boot app
VOLUME /tmp

# Step 3: Copy the JAR file into the container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Step 4: Expose the application port
EXPOSE 8080

# Step 5: Run the JAR file
ENTRYPOINT ["java", "-jar", "/app.jar"]