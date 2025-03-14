# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target directory to the container
COPY target/blog-app.jar app.jar

# Expose port 8080
EXPOSE 8080

# Define the entry point correctly (Fix: add a space between ENTRYPOINT and [ ])
ENTRYPOINT ["java", "-jar", "app.jar"]
