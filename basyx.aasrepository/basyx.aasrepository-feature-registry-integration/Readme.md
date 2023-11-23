# AssetAdministrationShell Repository - Registry Integration
This feature automatically integrates the Descriptor with the Registry while creation of the Shell at Repository. <br>
It also automatically removes the Descriptor from the Registry when the Shell is removed from the Repository. 

To enable this feature, following two properties should be configured:

```
basyx.aasrepository.feature.registryintegration = {AAS-Registry-Base-Url}
basyx.externalurl = {AAS-Repo-Base-Url}
```

This feature gets enable automatically when both of the above defined properties are configured, i.e., no external enabled/disabled property is required.

An example valid configuration:

```
basyx.aasrepository.feature.registryintegration = http://localhost:8050/api/v3.0
basyx.externalurl = http://localhost:8081
```