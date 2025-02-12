# Submodel Registry - Hierarchy

The Hierarchical SubmodelRegistry Feature enhances the availability of data across multiple registries by allowing retrieval requests to be delegated to another SubmodelRegistry.

This feature allows the creation of a hierarchical structure of registries, where a registry can delegate retrieval requests to another registry when a given descriptor is not found in its storage.

Retrieving all the stored SubmodelDescriptors with delegation is not currently supported.

## Configuration

### Enabling the Feature

To enable the Hierarchical SubmodelRegistry Feature, add the following property to your Spring application's configuration:

```properties
basyx.feature.hierarchy.enabled=true
```

### Delegation Strategy

Currently, only one delegation strategy is implemented:

#### Prefix Delegation Strategy

Delegates requests based on the `submodelDescriptorId` value. If the ID is an URL, a prefix (defaut `registry`) is appended to the URL and used as delegation endpoint.

The prefix can be configured by the property `basyx.feature.hierarchy.prefix`. Please refer to the example below:

```properties
basyx.feature.hierarchy.prefix=registry
```

If this property is left with an empty string, no prefix is appended to the URL contained in the `submodelDecriptorId`.
