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

##################nuevo#######################

# Stage 1: Build Angular application
#FROM node:20 AS angular-builder

# Set working directory for Angular
#WORKDIR /angular-app

# Copy package.json and package-lock.json from frontend directory
#COPY ../../../../frontend/package*.json ./

# Install Angular dependencies
#RUN npm install

# Copy Angular source code
#COPY ../../../../frontend/ ./

# Build Angular application
#RUN npm run build

# Stage 2: Build the Spring Boot application using Maven
#FROM maven:3.9.6-eclipse-temurin-21 AS spring-builder

# Set working directory inside the container
#WORKDIR /app

# Copy the Maven project descriptor file
#COPY ../../../../pom.xml .

# Copy the source code
#COPY ../../../../src ./src

# Create the directory for Angular static files
#RUN mkdir -p src/main/resources/public/new

# Copy the built Angular files from the angular-builder stage
#COPY --from=angular-builder /angular-app/dist/frontend/ ./src/main/resources/public/new/

# Build the project and skip tests
#RUN mvn clean package -DskipTests

# Stage 3: Create a lightweight image to run the application
#FROM eclipse-temurin:21-jdk

# Set working directory for the runtime container
#WORKDIR /app

# Copy the compiled JAR from the build stage
#COPY --from=spring-builder /app/target/app.jar app.jar

# Expose port 8443 to be used by the host
#EXPOSE 8443

# Set the default command to run the application
#ENTRYPOINT ["java", "-jar", "app.jar"]

#########################################################################
# Stage 1: Build Angular application
#FROM node:20 AS angular-builder

#WORKDIR /angular-app

#COPY ../../../../../frontend/package*.json ./
#RUN npm install

#COPY ../../../../../frontend/ ./
#RUN npm run build -- --configuration production --base-href=/spa/

# Stage 2: Build Spring Boot application
#FROM maven:3.9.6-eclipse-temurin-21 AS spring-builder

#WORKDIR /app

#COPY ../../../../../src/main/backend/pom.xml ./
#COPY ../../../../../src/main/backend/src ./src

#RUN mkdir -p src/main/resources/static/spa
#COPY --from=angular-builder /angular-app/dist/frontend/browser/ ./src/main/resources/static/spa/

#RUN mvn clean package -DskipTests

# Stage 3: Runtime container
#FROM eclipse-temurin:21-jdk

#WORKDIR /app

#COPY --from=spring-builder /app/target/app.jar app.jar

#EXPOSE 8443

#ENTRYPOINT ["java", "-jar", "app.jar"]
