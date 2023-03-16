FROM amazoncorretto:11
USER nobody
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} basyxExecutable.jar
ENTRYPOINT ["java","-jar","/basyxExecutable.jar", "--spring.config.location=file:/usr/share/config/application.properties"]