FROM eclipse-temurin:17
USER nobody
WORKDIR /application
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} basyxExecutable.jar

COPY src/main/resources/application.yml application.yml
COPY src/main/resources/application-MongoDB.yml application-MongoDB.yml
COPY src/main/resources/application-InMemory.yml application-InMemory.yml

ARG PORT=8081
ENV SERVER_PORT=${PORT}
ARG CONTEXT_PATH=/
ENV SERVER_SERVLET_CONTEXT_PATH=${CONTEXT_PATH}
EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=15s \
  CMD curl --fail http://localhost:${SERVER_PORT}${SERVER_SERVLET_CONTEXT_PATH%/}/actuator/health || exit 1

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar basyxExecutable.jar