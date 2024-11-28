# AssetAdministrationShell Repository - Registry Integration
This feature automatically integrates the Descriptor with the Registry while creation of the Shell at Repository. <br>
It also automatically removes the Descriptor from the Registry when the Shell is removed from the Repository. 

To enable this feature, the following two properties should be configured:

```
basyx.aasrepository.feature.registryintegration = {AAS-Registry-Base-Url}
basyx.externalurl = {AAS-Repo-Base-Url}
```

This feature gets enabled automatically when both of the above defined properties are configured, i.e., no external enabled/disabled property is required.

An example valid configuration:

```
basyx.aasrepository.feature.registryintegration = http://localhost:8050
basyx.externalurl = http://localhost:8081
```

## AAS Repository Integration with Authorized AAS Registry

If the target AAS Registry has authorization enabled, then the following properties needs to be configured in order to successfully integrate the Descriptor:

```
basyx.aasrepository.feature.registryintegration.authorization.enabled=true
basyx.aasrepository.feature.registryintegration.authorization.token-endpoint=http://localhost:9096/realms/BaSyx/protocol/openid-connect/token
basyx.aasrepository.feature.registryintegration.authorization.grant-type = <CLIENT_CREDENTIALS> or <PASSWORD>
basyx.aasrepository.feature.registryintegration.authorization.client-id = <client-id>
basyx.aasrepository.feature.registryintegration.authorization.client-secret = <client-secret>
basyx.aasrepository.feature.registryintegration.authorization.username=test
basyx.aasrepository.feature.registryintegration.authorization.password=test
basyx.aasrepository.feature.registryintegration.authorization.scopes=[]
```