# Use a base image with Java 21
FROM openjdk:21

# Copy the JAR package into the image
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8090 8092

# Run the App
ENTRYPOINT ["java", "-jar", "/app.jar"]