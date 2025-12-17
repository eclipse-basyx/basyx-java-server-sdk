# Stage 1: Download the AAS4J dependency
FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-17 AS maven-downloader
ARG HTTP_PROXY
ARG HTTPS_PROXY
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

ARG HTTP_PROXY
ARG HTTPS_PROXY
RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*
USER nobody

COPY target/*-exec.jar basyxExecutable.jar
COPY --from=maven-downloader /download/aas4j-model-${AAS4J_VERSION}.jar libs/

EXPOSE ${SERVER_PORT}

HEALTHCHECK --interval=10s --timeout=3s --retries=10 --start-period=5s \
  CMD curl --fail http://localhost:${SERVER_PORT}${SERVER_SERVLET_CONTEXT_PATH%/}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "basyxExecutable.jar"]
