# Eclipse BaSyx - AssetAdministrationShell Repository 
Eclipse BaSyx provides the AssetAdministrationShell Repository as off-the-shelf component:

docker run XXX

It supports DotAAS Part 1 V3 and the following HTTP/REST endpoints defined in [DotAAS Part 2 V3 - AssetAdministrationShell Repository](https://app.swaggerhub.com/apis/Plattform_i40/AssetAdministrationShellRepositoryServiceSpecification/V3.0_SSP-001):

* AAS Repository:
  * PostAssetAdministrationShell
  * GetAllAssetAdministrationShells
  * GetAssetAdministrationShellById
  * PutAssetAdministrationShellById
  * DeleteAssetAdministrationShellById
* AAS Service
  * GetAllSubmodelReferences
  * PostSubmodelReference
  * DeleteSubmodelReference
  * GetAssetInformation
  * PutAssetInformation

Right now, no additional input parameters modifying the output (e.g., cursor, serializationModifier) are supported.

In addition, it supports the following backends:
* InMemory
* MongoDB

Furthermore, the following features are provided:
* [MQTT eventing](basyx.aasrepository-feature-mqtt)

For a configuration example, see [application.properties](basyx.aasrepository.component/src/main/resources/application.properties)
