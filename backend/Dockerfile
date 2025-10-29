# Simple Docker configuration for Spring Boot backend
# This builds and runs the Java backend application

# Use Java 17 base image
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the entire project
COPY . .

# Build the application using Maven
RUN ./mvnw clean package -DskipTests

# Expose the port the app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/*.jar"]
