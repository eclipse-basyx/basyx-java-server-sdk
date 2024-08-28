# syntax=docker/dockerfile:1
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY ./initializer/pom.xml /workspace/pom.xml
COPY ./initializer/src /workspace/src
COPY ./realm/BaSyx-realm.json /workspace/BaSyx-realm.json
RUN mvn install

FROM keycloak/keycloak:24.0.4
COPY --from=build /workspace/target/org.eclipse.digitaltwin.basyx.v3.clients-keycloak-issuer-initializer.jar /opt/keycloak/providers/issuer-initializer.jar
COPY --from=build /workspace/BaSyx-realm.json /opt/keycloak/data/import/BaSyx-realm.json

RUN /opt/keycloak/bin/kc.sh import --file /opt/keycloak/data/import/BaSyx-realm.json