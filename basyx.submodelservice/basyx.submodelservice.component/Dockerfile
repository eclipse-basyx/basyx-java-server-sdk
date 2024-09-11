FROM amazoncorretto:17
USER nobody
WORKDIR /application
ARG JAR_FILE=target/*-exec.jar
COPY ${JAR_FILE} basyxExecutable.jar
ARG AAS4J_JAR_FILE=target/libs/aas4j-model-1.0.2.jar
COPY ${AAS4J_JAR_FILE} libs/aas4j-model-1.0.2.jar
ARG PORT=8081
ENV SERVER_PORT=${PORT}
ARG CONTEXT_PATH=/
ENV SERVER_SERVLET_CONTEXT_PATH=${CONTEXT_PATH}
EXPOSE ${SERVER_PORT}
HEALTHCHECK --interval=10s --timeout=3s --retries=10 --start-period=5s CMD curl --fail http://localhost:${SERVER_PORT}${SERVER_SERVLET_CONTEXT_PATH%/}/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "basyxExecutable.jar"]
