FROM amazoncorretto:11
USER nobody
ARG JAR_FILE=target/*.jar
COPY src/main/resources/application.properties /usr/share/config/application.properties
COPY ${JAR_FILE} basyxExecutable.jar
ENTRYPOINT ["java","-jar","/basyxExecutable.jar", "--spring.config.location=file:/usr/share/config/application.properties"]