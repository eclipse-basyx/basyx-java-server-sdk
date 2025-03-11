# 🚀 Eclipse BaSyx - Standalone Submodel Service Component

The **BaSyx Submodel Service Component** is a modular, Docker-based service that enables efficient deployment and management of submodels. This component allows you to deploy submodel services **without needing to build a Spring Boot application** or manually creating Dockerfiles.

*Invoke* calls to the submodel element *Operation* are delegated to Java classes that can either be precompiled or provided as source code. These Java methods can be provided as simple classes without additional dependencies. The `aas4j-model` module is already available at runtime, and additional libraries can be included via an environment variable.

---

## 📂 Project Structure

| Directory | Description |
|-----------|-------------|
| `src/main/java/` | Contains the core implementation of the submodel service. |
| `src/test/java/` | Includes unit and integration tests. |
| `src/main/resources/` | Stores default configuration files such as `application.yml`. |
| `example/` | Provides a sample setup using Docker Compose and dynamically compiled Java sources. |
| `target/` | Generated build artifacts (ignored in version control). |
| `Dockerfile` | Defines the standalone Docker image build process. |
| `pom.xml` | Maven build configuration. |

---

## 🔧 Setup & Installation

### **1️⃣ Building the Project with Maven**

To build the service manually, use:
```bash
mvn clean package
```
This will generate the JAR files inside the `target/` directory.

---

### **2️⃣ Building & Running with Docker**

To build the Docker image, **set the `docker.namespace` variable** before running the build:
```bash
export docker.namespace=my-docker-user
mvn clean package -Ddocker.namespace=my-docker-user
```
Then, to run the service in a container:
```bash
docker run -p 8081:8081 my-docker-user/basyx-submodel-service
```

---

### **3️⃣ Running with Docker Compose (Example Setup)**

A sample setup using Docker Compose is provided in the `example/` directory. To use it, navigate to the folder and execute:
```bash
cd example
./run-compose.sh
```
This starts the following services:
1. **`code-generator-jar`**: Dynamically compiles Java sources.
2. **`submodel-service`**: Loads the submodel and executes operations.

The JAR file is **built inside Docker Compose** for demonstration purposes, showcasing the dependency on `aas4j` at compile time. In production, it's recommended to build the JAR externally and mount it into the container.

Use *docker compose down* to tear down the stack;

---

## 🌍 Configuration

### **System Properties & Environment Variables**

Configuration can be specified either **as system properties** or **via environment variables**.

| Property | Example | Explanation |
|----------|---------|-------------|
| `basyx.submodelservice.submodel.file` | `mySubmodel.json` | Path to the submodel JSON file. |
| `basyx.submodelservice.feature.operation.dispatcher.enabled` | `true` | Enables operation dispatching. |
| `basyx.submodelservice.feature.operation.dispatcher.mappings[SquareOperation]` | `org.example.SquareOp` | Maps an `idShortPath` to a Java class. |
| `basyx.submodelservice.feature.operation.dispatcher.defaultMapping` | `org.example.MyOperation` | Default operation mapping if no specific one is found. |
| `basyx.submodelservice.feature.operation.dispatcher.java.sources` | `src` | Directory containing Java source files for runtime compilation. |
| `basyx.submodelservice.feature.operation.dispatcher.java.classes` | `classes` | Directory for storing compiled classes. |
| `basyx.submodelservice.feature.operation.dispatcher.java.additionalClasspath` | `jars/HelloWorld.jar` | Comma-separated list of additional JAR files for class loading. |

### Registry Integration Feature

The **BaSyx Submodel Service Component** supports automatic registration and deregistration of Submodel Descriptors in the Submodel Registry.

Configuration can be specified either **as system properties** or **via environment variables**. The following table summarizes the relevant properties:

| Property | Example | Explanation |
|----------|---------|-------------|
| `basyx.submodelservice.feature.registryintegration` | `http://localhost:8060` | Base URL of the Submodel Registry. |
| `basyx.externalurl` | `http://localhost:8081,http://docker-internal-url:8081` | External service base URL(s). |

For additional details on authorized registry integration, refer to the [Submodel Registry Integration Feature documentation](../basyx.submodelservice-feature-registry-integration/Readme.md).



---

## 🏗 Structure of Java Classes

Java classes that handle operations do not require dependencies and do not need to extend specific interfaces. A class should include a method with the following signature:

```java
public OperationVariable[] invoke(String path, Operation op, OperationVariable[] in)
```

Alternatively, a simplified version can be used:

```java
public OperationVariable[] invoke(OperationVariable[] in)
```

Example implementation:

```java
package org.basic;

import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.*;

public class AddOperation {
    public OperationVariable[] invoke(OperationVariable[] in) {
        Property first = (Property) in[0].getValue();
        Property second = (Property) in[1].getValue();
        int result = Integer.parseInt(first.getValue()) + Integer.parseInt(second.getValue());
        Property prop = new DefaultProperty.Builder().value(String.valueOf(result)).valueType(DataTypeDefXsd.INT).build();
        return new OperationVariable[] { new DefaultOperationVariable.Builder().value(prop).build() };
    }
}
```

The execution is **stateless**, meaning a new instance is created for each execution.

---

## 🛠 Creating a Custom Image

For quick deployment, a custom image can be built:

```dockerfile
FROM eclipsebasyx/submodel-service:0.2.0-SNAPSHOT
COPY sources/ /application/sources
COPY jars/ /application/jars
COPY submodel.json /application/submodel.json
COPY application.yml /application/config/application.yml
```

Operations can also be precompiled as JARs and placed in `/application/jars`. Alternatively, Java classes can be provided as source files in `/application/sources` and referenced via mapping properties.

---

## ✅ Running Tests

To execute the test suite, run:
```bash
mvn test
```
Integration tests are available for **MongoDB and InMemory** storage backends.

---

## 📌 Summary
✅ **Supports standalone execution via Docker or dynamic deployment via Docker Compose (example setup in `example/`).**  
✅ **Requires `docker.namespace` to be set before building the Docker image.**  
✅ **Demonstrates dynamic Java compilation inside Docker Compose to showcase runtime flexibility.**  
✅ **Easily configurable via system properties or environment variables (`application.yml`).**  

For further details, refer to the example setup in the [`example/`](example/) directory. 🚀

