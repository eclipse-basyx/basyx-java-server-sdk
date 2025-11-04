FROM maven:3.8.1-openjdk-17 AS build
ARG HTTP_PROXY
ARG HTTPS_PROXY
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17
ARG HTTP_PROXY
ARG HTTPS_PROXY
WORKDIR /app
COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
