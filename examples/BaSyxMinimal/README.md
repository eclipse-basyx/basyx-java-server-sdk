# BaSyx Infrastructure Setup

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

