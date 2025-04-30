# FROM eclipse-temurin:17
# RUN mkdir -p /application/classes
# RUN chmod -R a+rwx /application/classes
# USER nobody
# WORKDIR /application
# ARG JAR_FILE=target/*-exec.jar
# COPY ${JAR_FILE} basyxExecutable.jar
# ARG AAS4J_VERSION=${aas4j-version}
# ARG AAS4J_JAR_FILE=target/libs/aas4j-model-${AAS4J_VERSION}.jar
# COPY ${AAS4J_JAR_FILE} libs/aas4j-model-${AAS4J_VERSION}.jar
# ARG PORT=8081
# ENV SERVER_PORT=${PORT}
# ARG CONTEXT_PATH=/
# ENV SERVER_SERVLET_CONTEXT_PATH=${CONTEXT_PATH}
# EXPOSE ${SERVER_PORT}
# HEALTHCHECK --interval=10s --timeout=3s --retries=10 --start-period=5s CMD curl --fail http://localhost:${SERVER_PORT}${SERVER_SERVLET_CONTEXT_PATH%/}/actuator/health || exit 1
# ENTRYPOINT ["java", "-jar", "basyxExecutable.jar"]

# Stage 1: Download the AAS4J dependency
FROM maven:3.9-eclipse-temurin-17 AS maven-downloader
ARG AAS4J_VERSION=1.0.4
WORKDIR /download
RUN mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get \
      -Dartifact=org.eclipse.digitaltwin.aas4j:aas4j-model:${AAS4J_VERSION} \
 && mvn -B org.apache.maven.plugins:maven-dependency-plugin:3.6.0:copy \
      -Dartifact=org.eclipse.digitaltwin.aas4j:aas4j-model:${AAS4J_VERSION} \
      -DoutputDirectory=/download

# Stage 2: Build image
FROM eclipse-temurin:17
ARG AAS4J_VERSION=1.0.4
ARG PORT=8081
ARG CONTEXT_PATH=/
ENV SERVER_PORT=${PORT}
ENV SERVER_SERVLET_CONTEXT_PATH=${CONTEXT_PATH}
WORKDIR /application

RUN mkdir -p /application/classes /application/libs && \
    chmod -R a+rwx /application

USER nobody

COPY target/*-exec.jar basyxExecutable.jar
COPY --from=maven-downloader /download/aas4j-model-${AAS4J_VERSION}.jar libs/

EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=10s --timeout=3s --retries=10 --start-period=5s \
  CMD curl --fail http://localhost:${SERVER_PORT}${SERVER_SERVLET_CONTEXT_PATH%/}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "basyxExecutable.jar"]