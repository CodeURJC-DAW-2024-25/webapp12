# Etapa 1: construir la aplicación
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagen ligera solo con el .jar
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/target/app.jar app.jar
EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]
