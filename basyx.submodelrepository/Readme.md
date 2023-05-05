# Eclipse BaSyx - Submodel Repository 
Eclipse BaSyx provides the Submodel Repository as off-the-shelf component:

    docker run --name=sm-repo -p:8081:8081 -v C:/tmp:/usr/share/config eclipsebasyx/submodel-repository:2.0.0-SNAPSHOT 

It supports DotAAS Part 1 V3 and the following HTTP/REST endpoints defined in [DotAAS Part 2 V3 - Submodel Repository](https://app.swaggerhub.com/apis/Plattform_i40/SubmodelRepositoryServiceSpecification/V3.0_SSP-001):

* Submodel Repository
  * GetAllSubmodels
  * GetSubmodelById (including ValueOnly, Metadata)
  * PostSubmodel
  * PutSubmodelById
  * DeleteSubmodelById
* Submodel Service
  * GetAllSubmodelElements
  * PostSubmodelElement
  * PostSubmodelElementByPath
  * DeleteSubmodelElementByPath
  * PatchSubmodelElementByPath (including ValueOnly)
  * GetSubmodelElementValueByPath

Right now, no additional input parameters modifying the output (e.g., cursor, serializationModifier) are supported, if not specified otherwise.

In addition, it supports the following backends:
* InMemory

Furthermore, the following features are provided:
* [MQTT eventing](basyx.submodelrepository-feature-mqtt)

For a configuration example, see [application.properties](basyx.submodelrepository.component/src/main/resources/application.properties)
