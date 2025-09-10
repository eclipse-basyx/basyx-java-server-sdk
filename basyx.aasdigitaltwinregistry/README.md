

# BaSyx Digital Twin Registry

## Overview
The **Digital Twin Registry** serves as a combined module that merges the capabilities of `AASRegistry` and `AASDiscovery`.  
When a client calls the `/shell-description` endpoint, the module dynamically constructs both an `AssetAdministrationShellDescriptor` and an `aasDiscoveryDocumentEntity`.

This dual-output ensures that the asset shell becomes immediately discoverable and accessible, blending registry and discovery functionalities in a seamless operation.

---

## How It Works

- **Endpoint Integration**

  A single REST endpoint (`/shell-description`) triggers the generation of:
    - An **AAS Descriptor**, representing the asset's metadata and management interface.
    - A **Discovery Document**, enabling other components to locate or resolve the AAS.

- **Unified Workflow**  
  By combining `AASRegistry` and `AASDiscovery`, the module streamlines the typical sequential two-step — *discover then retrieve* — into a single integrated operation.

---

## Module Structure in the BaSyx SDK

- **New Module Introduction**  
  Within the main BaSyx SDK, a new module — `digitaltwinregistry` — has been introduced.  
  It follows the **decorator pattern**, meaning it wraps around existing functionality to extend behavior without modifying original code.

- **Delegate-Based Design**  
  At its core, the module implements or creates a **delegate** for the `ShellDescriptorsApiDelegate` interface.  
  This delegate intercepts API calls (particularly related to shell descriptions) and injects the registry-and-discovery logic — making the module effectively pluggable and maintainable.

---

## Summary

In essence, the **Digital Twin Registry module**:

- Combines **registry** and **discovery** into a unified action via `/shell-description`.
- Is implemented as a **decorator delegate** (`ShellDescriptorsApiDelegate`), making it both modular and maintainable.
- Seamlessly integrates with existing BaSyx storage options and aligns with broader architectural goals, such as centralized registries, tagging, and scalable discovery.

## Environment
This document describes the environment variables used to configure the BaSyx Digital Twin Registry application. The application supports multiple profiles with different storage backends.

---

## Configuration Files
The application uses three YAML configuration files:

- `application.yml` - Base configuration
- `application-InMemory.yml` - In-memory storage profile
- `application-MongoDB.yml` - MongoDB storage profile

---

## Environment Variables

### Base Configuration (`application.yml`)

| Environment Variable | Default Value | Description |
|-----------------------|---------------|-------------|
| `SPRING_PROFILE`      | MongoDB       | Active Spring profile (`InMemory` or `MongoDB`) |
| `LOGGING_LEVEL`       | INFO          | Logging level for root and BaSyx components |

---

### Server Configuration

| Property       | Default Value | Description |
|----------------|---------------|-------------|
| `server.port`  | 8081          | HTTP server port |

---

### CORS Configuration

| Property                       | Value                                               | Description |
|--------------------------------|-----------------------------------------------------|-------------|
| `basyx.cors.allowed-methods`   | GET,POST,PATCH,DELETE,PUT,OPTIONS,HEAD              | Allowed HTTP methods |
| `basyx.cors.allowed-origins`   | *                                                   | Allowed origins (CORS) |

---

### Management Endpoints

| Property                                 | Value                        | Description |
|------------------------------------------|------------------------------|-------------|
| `management.endpoints.web.exposure.include` | health,metrics,mappings     | Exposed actuator endpoints |

---

### SpringDoc/Swagger Configuration

| Property                          | Value              | Description |
|-----------------------------------|--------------------|-------------|
| `springdoc.api-docs.enabled`      | true               | Enable API documentation |
| `springdoc.swagger-ui.enabled`    | true               | Enable Swagger UI |
| `springdoc.swagger-ui.path`       | /swagger-ui.html   | Swagger UI path |
| `springdoc.swagger-ui.csrf.enabled` | false             | Disable CSRF protection for Swagger |

---

## InMemory Profile Configuration

**Profile Name:** `InMemory`

### Environment Variables
_No additional environment variables required for InMemory profile._

### Configuration Properties

| Property            | Value      | Description |
|---------------------|------------|-------------|
| `basyx.backend`     | InMemory   | Use in-memory storage backend |
| `registry.type`     | InMemory   | Registry type |
| `registry.discovery.enabled` | true | Enable discovery service |

### Auto-configuration Exclusions
The InMemory profile excludes MongoDB auto-configuration:

- `org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration`
- `org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration`

---

## MongoDB Profile Configuration

**Profile Name:** `MongoDB`

### Environment Variables

| Environment Variable     | Default Value                           | Description |
|---------------------------|-----------------------------------------|-------------|
| `AUTHENTICATION_DATABASE` | aasregistry                             | MongoDB authentication database name |
| `DATABASE_HOST`           | localhost                               | MongoDB host address |
| `DATABASE_PORT`           | localhost                               | MongoDB port (**Note:** should be a numeric port) |
| `DATABASE_USERNAME`       | smartsystemhub                          | MongoDB username |
| `DATABASE_PASSWORD`       | smartsystemshubdatabaseforfactoryX      | MongoDB password |

### Configuration Properties

| Property            | Value   | Description |
|---------------------|---------|-------------|
| `basyx.backend`     | MongoDB | Use MongoDB storage backend |
| `registry.type`     | MongoDB | Registry type |
| `registry.discovery.enabled` | true | Enable discovery service |
| `basyx.aasdiscoveryservice.mongodb.collectionName` | aasregistry | MongoDB collection name |