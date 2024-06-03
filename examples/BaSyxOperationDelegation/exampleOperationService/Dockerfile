# Stage 1: Build Stage
FROM maven:3.8.1-openjdk-17 AS build
WORKDIR /app
# Kopiere die pom.xml und lade die Abhängigkeiten herunter
COPY pom.xml .
RUN mvn dependency:go-offline
# Kopiere den restlichen Quellcode
COPY src src
# Baue das Projekt
RUN mvn clean package -DskipTests

# Stage 2: Runtime Stage
FROM openjdk:17
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Starte die Anwendung
ENTRYPOINT ["java", "-jar", "app.jar"]
