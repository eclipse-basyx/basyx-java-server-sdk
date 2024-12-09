# Submodel Registry - Hierarchy - Example

This example showcases the working principle of the hierarchical registries feature.

## Scenario description

```mermaid
sequenceDiagram
    actor Client
    participant Root as Root Registry <submodel-registry-root:8080>
    participant Delegated as Delegated Registry <registry.delegated-submodel-registry:8080>

    Client ->> Root: Request resolution for Submodel-ID "http://delegated-submodel-registry:8080/test/submodel"
    activate Root

    Root ->> Root: Check local records
    alt Not found locally
        Root ->> Root: Determine registry based on URI prefix
        Root ->> Delegated: Resolve Submodel-ID "http://delegated-submodel-registry:8080/test/submodel" at registry.delegated-submodel-registry:8080
        activate Delegated
        Delegated ->> Delegated: Resolve Submodel-
        Delegated ->> Root: Resolution result
        deactivate Delegated
    end

    Root ->> Client: Return resolution result
    deactivate Root
```

## Running the scenario

In order to run the example, please make sure that all submodel registries maven modules are correctly installed in your local Maven repository.

1. Generate the Docker image: `mvn clean install -Ddocker.namespace=submodel-registry-test`

2. Run the docker compose: `docker compose up`

Two containers should start: (1) one for the root Submodel Registry - to which the http request are going to be made; (2) one for the delegated Submodel Registry - to which requests may be delegated to.

They are visibile within the bridged Docker network as (1) submodel-registry-root:8080 and (2) registry.delegated-submodel-registry:8080

3. Run the scenario [HierarchicalSubmodelRegistryIT](/src/test/java/org/eclipse/digitaltwin/basyx/submodelregistry/feature/hierarchy/example/HierachicalSubmodelRegistryIT.java)
