# BaSyx Digital Twin Registry - Environment Configuration

## Overview
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
