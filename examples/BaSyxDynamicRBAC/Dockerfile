ARG BASYX_VERSION=2.0.0-SNAPSHOT

# Build the final image based on the local submodel-repository image.
FROM eclipsebasyx/submodel-repository:${BASYX_VERSION}

# Switch to root user to install dependencies
USER root

RUN apt-get update && apt-get install -y --no-install-recommends jq \
    && rm -rf /var/lib/apt/lists/*

# Copy the file from local system to Docker container
COPY ./initial-submodel.json /application/initial-submodel.json

# Expose necessary ports
EXPOSE 8081

# Set the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "basyxExecutable.jar"]
