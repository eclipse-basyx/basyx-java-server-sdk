# BaSyx Roadmap
Here, a list of features, components and general updates that are planned in the future is described. Please note that this list is not exhaustive and repriorization might happen.

## BaSyx AAS Core
- Implementation of AAS API Spec as well as upcoming features (continuous)
- AAS Client SDK (continuous)
- Dynamic integration of features/backends in AAS Infrastructure OTS components (Q4/2024)
- ABAC support for all BaSyx components (Q1/2025)
- Code Generation based on AASX files (Client, ...) (Q1/2025)
- Gateway component for distribution of AASs and submodels across distributed registries and repositories (Q1/2025)
  - Registry Integration will be moved to this component
  - AAS Preconfiguration will move to this component
  - AAS Discovery Integration will be implemented as part of this component 
- Support for the Eclipse Dataspace Connector (Q2/2025)
- Benchmarking tool for AAS API components (Q2/2025)

## BaSyx AAS Integration Components
### DataBridge 
For more details, see [basyx-databridge](https://github.com/eclipse-basyx/basyx-databridge)
- Support for Asset Interface Description Submodel and Asset Interface Mapping Configuration Submodel (Q4/2024)
- GUI-based Configuration (Q1/2025)
- RBAC support for the DataBridge (Q1/2025)
- Integration of more complex data transformations, e.g., data aggregation of historic data (Q2/2025)
