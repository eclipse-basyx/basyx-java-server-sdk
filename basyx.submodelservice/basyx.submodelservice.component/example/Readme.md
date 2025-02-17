# 🚀 Submodel Service Component Example

The **Submodel Service Component** is a versatile, Docker-based service designed for efficiently deploying and running submodels. The service can be configured dynamically using environment variables, configuration files, or prebuilt Docker images.

---

## ⚡ Quick Start

To get started quickly, use one of the provided shell scripts:

```bash
# Standalone mode - builds and runs a preconfigured Docker image
./run-standalone.sh

# Docker Compose mode - dynamically compiles Java sources and starts the service
./run-compose.sh
```

Once the service is running, test it using:

```bash
./run-tests.sh
```

This script executes predefined operations on the submodel to verify functionality.

---

## 🔧 Deployment Options

### 1️⃣ Standalone Mode (Prebuilt Image)

- Uses `Dockerfile.standalone-example` to create a fully self-contained image.
- Includes all necessary components (Java classes, configuration files) within the image.
- Optimized for **faster startup times** by eliminating runtime compilation.

### 2️⃣ Docker Compose Mode (Dynamic Setup)

- Uses `docker-compose.yml` to launch multiple services.
- Dynamically compiles Java sources before starting the submodel service.
- Ideal for **flexibility**, allowing modifications and extensions without rebuilding the image.
- **Note:** Typically, JAR files are built outside Docker Compose and mounted from the host system. However, in this setup, the JAR is generated within the `code-generator-jar` service to illustrate the build process and demonstrate the need for `aas4j` during compilation. At runtime, `aas4j` is not required.

---

## ⚙️ Docker Compose Configuration

The **[docker-compose.yml](docker-compose.yml)** file defines two primary services:

1. **`code-generator-jar`**: Compiles Java sources at runtime and provides a JAR file.
2. **`submodel-service`**: Loads precompiled JARs and dynamically compiles additional sources.

### 📂 Volume Mappings

| Mounted Path                                                                 | Purpose                                                        |
| ---------------------------------------------------------------------------- | -------------------------------------------------------------- |
| `./submodel.json:/application/submodel.json:ro`                              | Supplies the submodel definition                               |
| `./sources/:/application/sources/:ro`                                        | Provides Java source files for dynamic compilation             |
| `jar-volume:/application/jars/:ro`                                           | Stores the prebuilt `HelloWorld.jar` from `code-generator-jar` |
| `./application-mappings.yml:/application/config/application-mappings.yml:ro` | Contains `idShortPath` mappings                                |

---

## 🌍 Environment Variables

The following environment variables configure the service:

| Variable                                                                      | Description                                     |
| ----------------------------------------------------------------------------- | ----------------------------------------------- |
| `BASYX_SUBMODELSERVICE_SUBMODEL_FILE`                                         | Path to the submodel JSON file                  |
| `BASYX_SUBMODELSERVICE_FEATURE_OPERATION_DISPATCHER_JAVA_SOURCES`             | Java source directory (for runtime compilation) |
| `BASYX_SUBMODELSERVICE_FEATURE_OPERATION_DISPATCHER_JAVA_ADDITIONALCLASSPATH` | Path to additional JARs                         |
| `SPRING_PROFILES_ACTIVE`                                                      | Specifies the profile to use (e.g., `mappings`) |

---

## 🚀 Running the Service

### 1️⃣ Standalone Mode

```bash
./run-standalone.sh
```

This builds the **Dockerfile.standalone-example** image and runs it as a container.

### 2️⃣ Docker Compose Mode

```bash
./run-compose.sh
```

- The `code-generator-jar` service **compiles and stores** `HelloWorld.jar`.
- The `submodel-service` container **loads** the JAR and compiles additional sources.

**Note:** Since both modes bind to the same port, only one should be active at a time.

---

## ✅ Testing the Image

After starting the service, execute:

```bash
./run-tests.sh
```

Follow the script's instructions to interact with the submodel and validate its operations.

---

## 📌 Summary

✅ **Standalone Mode:** Uses a precompiled Docker image for fast deployment.

✅ **Docker Compose Mode:** Dynamically compiles Java sources and integrates prebuilt JAR files.

✅ **Code Generator Service:** Generates Java classes and provides them to the submodel.

✅ **Comprehensive Testing:** Run `./run-tests.sh` to verify the service functionality.


