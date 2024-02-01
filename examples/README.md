# BaSyx V2 Examples
This folder contains example configurations and docker compose files for setting up the BaSyx V2 infrastructure.


## Standalone Submodel
For definining standalone submodels, see [Submodel Service](../basyx.submodelservice)

## Infrastructure Example

After executing 

```bash
docker-compose up -d
```

the following components are started:
* AAS Repository (http://localhost:8081/shells)
* Submodel Repository (http://localhost:8081/submodels)
* ConceptDescription Repository (http://localhost:8081/concept-descriptions)
* AAS Registry (http://localhost:8082/shell-descriptors)
* Submodel Registry (http://localhost:8083/submodel-descriptors)
* AAS Discovery (http://localhost:8084/lookup/shells)
* AAS Web UI (http://localhost:3000)

By leveraging the registry integration features of AAS Repository and Submodel Repository, the preconfigured AAS Environment serializations are loaded and automatically registered.

## BaSyx with NGINX
See the separate [NGINX example](BaSyxNGINX) for a comprehensive setup leveraging NGINX.

## BaSyx Time Series Data Example
See the separate [Time Series example](https://github.com/eclipse-basyx/basyx-applications/tree/main/aas-gui/examples/TimeSeriesData) for a comprehensive setup leveraging InfluxDB.
