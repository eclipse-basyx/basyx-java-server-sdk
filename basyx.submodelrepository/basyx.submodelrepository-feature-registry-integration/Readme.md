# Submodel Repository - Registry Integration
This feature automatically integrates the Descriptor with the Registry while creation of the Submodel at the Repository. <br>
It also automatically removes the Descriptor from the Registry when the Submodel is removed from the Repository.

To enable this feature, the following two properties should be configured:

```
basyx.submodelrepository.feature.registryintegration = {Submodel-Registry-Base-Url}
basyx.externalurl = {Submodel-Repo-Base-Url}
```

This feature gets enable automatically when both of the above defined properties are configured, i.e., no external enabled/disabled property is required.

An example valid configuration:

```
basyx.submodelrepository.feature.registryintegration = http://localhost:8060
basyx.externalurl = http://localhost:8081
```
