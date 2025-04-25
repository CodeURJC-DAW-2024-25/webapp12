# Stage 1: Build Angular application
FROM node:20 AS angular-builder

WORKDIR /angular-app

COPY frontend/package*.json ./
RUN npm install

COPY frontend/ ./
RUN npm run build -- --configuration production --base-href=/new/

# Stage 2: Build Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21 AS spring-builder

WORKDIR /app

# Copiamos pom.xml y código fuente del backend
COPY src/main/backend/pom.xml pom.xml
COPY src/main/backend/src src

# 🔐 Aseguramos que el keystore se incluya (muy importante)
COPY src/main/backend/src/main/resources/keystore.jks src/main/resources/

# Copiamos los archivos estáticos de Angular dentro del backend
RUN mkdir -p src/main/resources/static/new
COPY --from=angular-builder /angular-app/dist/frontend/browser/ src/main/resources/static/new/

# Compilamos el backend
RUN mvn clean package -DskipTests

# Stage 3: Runtime container
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=spring-builder /app/target/app.jar app.jar

EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
