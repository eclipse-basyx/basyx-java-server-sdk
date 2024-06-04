# AssetAdministrationShell Registry - Hierarchy

The Hierarchical AasRegistry Feature enhances the availability of data across multiple registries by allowing retrieval requests to be delegated to another AasRegistry.

This feature allows the creation of a hierarchical structure of registries, where a registry can delegate retrieval requests to another registry when a given descriptor is not found in its storage.

## Configuration

### Enabling the Feature

To enable the Hierarchical AasRegistry Feature, add the following property to your Spring application's configuration:

```properties
basyx.aasregistry.feature.hierarchy.enabled=true
```

The next step is to set up the desired delegation strategy.

### Delegation Strategy

Currently, only one delegation strategy is implemented:

#### Direct Url Delegation Strategy

Delegates requests directly to a URL specified by the `basyx.aasregistry.feature.hierarchy.delegatedUrl` property. For example:

```properties
basyx.aasregistry.feature.hierarchy.delegatedUrl=http://localhost:8050
```
