# Use Ubuntu as the base image
FROM ubuntu:20.04 AS installer

# Switch to root user to install dependencies (default user is root in Ubuntu)
USER root

# Install curl (to download jq) and other dependencies
RUN apt-get update && apt-get install -y \
    curl \
    libcurl4-openssl-dev \
    wget

# Download jq version 1.4 binary from GitHub releases
RUN wget -O /usr/local/bin/jq https://github.com/jqlang/jq/releases/download/jq-1.4/jq-linux-x86_64

# Make jq executable
RUN chmod +x /usr/local/bin/jq

# Stage 2: Build the final image based on the submodel-repository image
FROM eclipsebasyx/submodel-repository:2.0.0-milestone-04

# Switch to root user to install dependencies
USER root

# Copy jq binary from the Ubuntu stage to the final image (submodel-repository)
COPY --from=installer /usr/local/bin/jq /usr/local/bin/jq

# Copy the file from local system to Docker container
COPY ./initial-submodel.json /application/initial-submodel.json

# Expose necessary ports
EXPOSE 8081

# Set the entrypoint to run the JAR file
ENTRYPOINT ["java", "-jar", "basyxExecutable.jar"]
