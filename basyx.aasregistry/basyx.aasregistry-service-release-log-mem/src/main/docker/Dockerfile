FROM openjdk:11-jre-slim as builder
COPY maven/${project.build.finalName}.jar ./
RUN java -Djarmode=layertools -jar ${project.build.finalName}.jar extract

FROM openjdk:11-jre-slim
RUN mkdir /workspace
WORKDIR /workspace
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
RUN true
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=logEvents,inMemoryStorage
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom", "org.springframework.boot.loader.JarLauncher"]

