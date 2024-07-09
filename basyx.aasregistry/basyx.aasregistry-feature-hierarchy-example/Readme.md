# AssetAdministrationShell Registry - Hierarchy - Example

This example showcases the working principle of the hierarchical registries feature.

## Scenario description

```mermaid
sequenceDiagram
    actor Client
    participant Root as Root Registry <aas-registry-root:8080>
    participant Delegated as Delegated Registry <registry.delegated-aas-registry:8080>

    Client ->> Root: Request resolution for AAS-ID "http://delegated-aas-registry:8080/test/aas"
    activate Root

    Root ->> Root: Check local records
    alt Not found locally
        Root ->> Root: Determine registry based on URI prefix
        Root ->> Delegated: Resolve AAS-ID "http://delegated-aas-registry:8080/test/aas" at registry.delegated-aas-registry:8080
        activate Delegated
        Delegated ->> Delegated: Resolve AAS-ID
        Delegated ->> Root: Resolution result
        deactivate Delegated
    end

    Root ->> Client: Return resolution result
    deactivate Root
```

## Running the scenario

In order to run the example, please make sure that all aasregistries maven modules are correctly installed in your local Maven repository.

1. Generate the Docker image: `mvn clean install -Ddocker.namespace=aas-registry-test`

2. Run the docker compose: `docker compose up`

Two containers should start: (1) one for the root AAS Registry - to which the http request are going to be made; (2) one for the delegated AAS Registry - to which requests may be delegated to.

They are visibile within the bridged Docker network as (1) aas-registry-root:8080 and (2) registry.delegated-aas-registry:8080

3. Run the scenario [HierarchicalAasRegistryIT](/src/test/java/org/eclipse/digitaltwin/basyx/aasregistry/feature/hierarchy/example/HierachicalAasRegistryIT.java)
