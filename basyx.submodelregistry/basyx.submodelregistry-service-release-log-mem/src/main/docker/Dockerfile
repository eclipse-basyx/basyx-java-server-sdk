FROM eclipse-temurin:17 as builder
WORKDIR /workspace
ARG HTTP_PROXY
ARG HTTPS_PROXY
ARG FINAL_NAME=${project.build.finalName}
COPY maven/${FINAL_NAME}.jar ./
RUN java -Djarmode=tools -jar ${FINAL_NAME}.jar extract --layers --launcher --destination . --force

FROM eclipse-temurin:17
ARG HTTP_PROXY
ARG HTTPS_PROXY
RUN apt-get update && apt-get install -y ca-certificates && rm -rf /var/lib/apt/lists/*
RUN mkdir /workspace
WORKDIR /workspace
COPY --from=builder /workspace/dependencies/ ./
COPY --from=builder /workspace/snapshot-dependencies/ ./
RUN true
COPY --from=builder /workspace/spring-boot-loader/ ./
COPY --from=builder /workspace/application/ ./
ENV SPRING_PROFILES_ACTIVE=logEvents,inMemoryStorage
ARG PORT=8080
ENV SERVER_PORT=${PORT}
ARG CONTEXT_PATH=/
ENV SERVER_SERVLET_CONTEXT_PATH=${CONTEXT_PATH}
EXPOSE ${SERVER_PORT}
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=15s CMD curl --fail http://localhost:${SERVER_PORT}${SERVER_SERVLET_CONTEXT_PATH%/}/actuator/health || exit 1
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom", "org.springframework.boot.loader.launch.JarLauncher"]

