FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/reservas-laboratorios-api-1.0.0.jar /app/app.jar

# Expose the port that the Spring Boot app will run on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
