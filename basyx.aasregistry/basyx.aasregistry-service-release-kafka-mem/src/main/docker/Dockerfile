FROM amazoncorretto:17.0.8-alpine3.18 as builder
COPY maven/${project.build.finalName}.jar ./
RUN java -Djarmode=layertools -jar ${project.build.finalName}.jar extract

FROM amazoncorretto:17.0.8-alpine3.18
RUN mkdir /workspace
WORKDIR /workspace
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
RUN true
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=kafkaEvents,inMemoryStorage
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom", "org.springframework.boot.loader.JarLauncher"]

