FROM eclipse-temurin:17
ARG HTTP_PROXY
ARG HTTPS_PROXY
USER nobody
WORKDIR /application
ARG JAR_FILE=target/*-exec.jar
COPY ${JAR_FILE} basyxExecutable.jar
COPY src/main/resources/application.properties application.properties
ARG PORT=8081
ENV SERVER_PORT=${PORT}
EXPOSE ${SERVER_PORT}
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=15s CMD curl --fail http://localhost:${SERVER_PORT}/actuator/health || exit 1
ENTRYPOINT ["java","-jar","basyxExecutable.jar"]
