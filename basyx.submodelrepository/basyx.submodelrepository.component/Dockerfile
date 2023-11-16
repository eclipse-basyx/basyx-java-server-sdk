FROM amazoncorretto:17
USER nobody
WORKDIR /application
ARG JAR_FILE=target/*-exec.jar
COPY ${JAR_FILE} basyxExecutable.jar
COPY src/main/resources/application.properties application.properties
ARG PORT=8081
ENV SERVER_PORT=${PORT}
EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","basyxExecutable.jar"]
