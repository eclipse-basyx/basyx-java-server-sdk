## Submodel Service Component Example

This example describes a potential setup for the Submodel Service using Docker Compose.

## Configuration

The [docker-compose.yml](docker-compose.yml) file provides a basic setup for starting the service. The Docker image used is initially built based on the [Dockerfile located in the parent directory](../Dockerfile).

Volumes are used to provide the Submodel and the executable source code to the container, which are referenced in the environment section. The mapping of `idShortPath` to Java classes is also referenced there and loaded via [application-mappings.yml](application-mappings.yml). Alternatively, you can simplify the setup by configuring everything directly in [application.yml](application.yml).

**Performance Note:** For faster startup times, it's recommended to pre-compile your code and provide it as JAR files or class files. The [aas4j](https://github.com/eclipse-aas4j/aas4j) model classes are available at runtime and do not need to be added to the classpath.

### Test Script

Please review the [start-container.sh](start-container.sh) shell script and execute it. The script first builds the executable JAR using Maven, if necessary, which is then copied into the Docker image. After that, the Docker Compose stack is started, and test cases are executed.

## Standalone Image

The [Dockerfile.standalone-example](Dockerfile.standalone-example) provides an example of how a Dockerfile for a standalone service might look. When using standalone images, mounting additional volumes or setting environment variables is not strictly necessary. For standalone images, it's recommended to provide precompiled classes or a JAR file to expedite the service startup. The `aas4j` model classes are available at runtime and do not need to be added to the classpath.
