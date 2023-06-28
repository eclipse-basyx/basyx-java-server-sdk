# Basyx AAS Registry Client Native

This is the generated java openAPI client (based on native java with jackson parsing) that can be used to communicate with the AAS registry server.

To use the client in your maven projects define the following dependency:
```xml
<dependency>
		<groupId>org.eclipse.digitaltwin.basyx.aasregistry</groupId>
		<artifactId>basyx.aasregistry-client-native</artifactId>
</dependency>
```

If you also want to use the search API we highly recommend that you also include the search path builder class:
```xml
<dependency>
		<groupId>dorg.eclipse.digitaltwin.basyx.aasregistry</groupId>
		<artifactId>basyx.aasregistry-paths</artifactId>
</dependency>
```

The search API does not only provide concrete filtering but also similarity matches like words in a longer string. This code, for example, is a match for submodels that contain the word *robot* in their description: 

```java
new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(AasRegistryPaths.submodelDescriptors().description().text()).value("robot");
```

We also support these regular expressions. A query can be created like this:

```java
new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.submodelDescriptors().description().text()).value("r[ob]{3}t");
```

As response to a search query, the client will receive a list of the filtered *AssetAdministrationShellDescriptors*. If you have enabled pagination, the list will only contain a subset of all results. By addressing an attribute of a submodel descriptor, the matching descriptors are shrunk so that they contain only matching submodels.


It is also possible to filter by shell and submodel extensions. Define the *extensionName*, reference the extension value property and define the value for equals or regex matching:

```java
new ShellDescriptorQuery().path(AasRegistryPaths.extensions().value()).extensionName("TAG").value("private");
```

If you have multiple queries that you want to aggregate in the request, use *combinedWith* to chain the requests: 

```java
ShellDescriptorQuery queryA = new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.idShort()).value("short-id");
ShellDescriptorQuery queryB = new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.extensions().value()).extensionName("TAG").value("private");
ShellDescriptorQuery queryC = new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.submodelDescriptors().isShort()).value("sm_id_short");
queryA.combinedWith(queryB);
queryB.combinedWith(queryC);
```

The query matches, if all shell-related queries match and ,if there are also queries to submodel content (path starts with *"submodelDescriptors."*), there is at least one submodel that matches all submodel queries.