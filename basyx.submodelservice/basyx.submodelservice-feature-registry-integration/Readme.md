
# SubmodelService Registry Integration

This feature automatically registers the Submodel Descriptor with the configured Submodel Registry upon startup and automatically deregisters it upon shutdown of the Submodel Service.

## Configuration

To enable the integration feature, configure the following properties in your application:

```properties
basyx.submodelservice.feature.registryintegration={SM-Registry-Base-Url}
basyx.externalurl={AAS-External-Service-Base-Url}
```

**Example:**
```properties
basyx.submodelservice.feature.registryintegration=http://localhost:8060
basyx.externalurl=http://localhost:8081
```

## Authorization Support

If the target Submodel Registry requires authorization, enable and configure it as follows:

```properties
basyx.submodelservice.feature.registryintegration.authorization.enabled=true
basyx.submodelservice.feature.registryintegration.authorization.client-id=<client-id>
basyx.submodelservice.feature.registryintegration.authorization.client-secret=<client-secret>
basyx.submodelservice.feature.registryintegration.authorization.token-endpoint=http://localhost:9090/oauth/token
```

If the OAuth2 grant type is **password**, additionally specify:

```properties
basyx.submodelservice.feature.registryintegration.authorization.username=<username>
basyx.submodelservice.feature.registryintegration.authorization.password=<password>
```

### Example Configuration

A typical setup without authorization:

```properties
basyx.submodelservice.feature.registryintegration=http://localhost:8060
basyx.externalurl=http://localhost:8081
```

A setup **with authorization** enabled might look like:

```properties
basyx.submodelservice.feature.registryintegration=http://localhost:8060
basyx.externalurl=http://localhost:8081

basyx.submodelservice.feature.registryintegration.authorization.enabled=true
basyx.submodelservice.feature.registryintegration.authorization.token-endpoint=http://auth-server/token
basyx.submodelservice.feature.registryintegration.authorization.client-id=myClientId
basyx.submodelservice.feature.registryintegration.authorization.client-secret=mySecret
basyx.submodelservice.feature.registryintegration.authorization.username=user
basyx.submodelservice.feature.registryintegration.authorization.password=pass
```

**Note:**  
- The feature requires `basyx.externalurl` to be set explicitly to provide the correct external reference URL for the Submodel Service.
- If both properties are configured correctly, the integration is automatically activated.