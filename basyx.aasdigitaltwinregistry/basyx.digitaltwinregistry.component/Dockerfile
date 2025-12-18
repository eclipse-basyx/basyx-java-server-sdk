FROM eclipse-temurin:17
ARG HTTP_PROXY
ARG HTTPS_PROXY
RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*
USER nobody
WORKDIR /application
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} basyxExecutable.jar

COPY src/main/resources/application.properties application.properties
COPY src/main/resources/application-MongoDB.properties application-MongoDB.properties
COPY src/main/resources/application-InMemory.properties application-InMemory.properties

ARG PORT=8081
ENV SERVER_PORT=${PORT}
ARG CONTEXT_PATH=/
ENV SERVER_SERVLET_CONTEXT_PATH=${CONTEXT_PATH}
EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=15s \
  CMD curl --fail http://localhost:${SERVER_PORT}${SERVER_SERVLET_CONTEXT_PATH%/}/actuator/health || exit 1

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar basyxExecutable.jar