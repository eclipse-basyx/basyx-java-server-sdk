FROM quay.io/keycloak/keycloak:24.0.4
ARG HTTP_PROXY
ARG HTTPS_PROXY
# Make the realm configuration available for import
COPY /realm/BaSyx-realm.json /opt/keycloak_import/

# Import the realm and user
RUN /opt/keycloak/bin/kc.sh import --file /opt/keycloak_import/BaSyx-realm.json

# The Keycloak server is configured to listen on port 8080
EXPOSE 8080
EXPOSE 8443

# Import the realm on start-up
CMD ["start-dev"]