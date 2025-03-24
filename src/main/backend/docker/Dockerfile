# Stage 1: Build the application using Maven inside a Docker container
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy the Maven project descriptor file
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the project and skip tests
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight image to run the application
FROM eclipse-temurin:21-jdk

# Set working directory for the runtime container
WORKDIR /app

# Copy the compiled JAR from the build stage
COPY --from=builder /app/target/app.jar app.jar

# Expose port 8443 to be used by the host
EXPOSE 8443

# Set the default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

