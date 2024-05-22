# Eclipse BaSyx - Submodel Repository 
Eclipse BaSyx provides the Submodel Repository as off-the-shelf component:

    docker run --name=sm-repo -p:8081:8081 -v C:/tmp/application.properties:/application/application.properties eclipsebasyx/submodel-repository:2.0.0-SNAPSHOT 

> *Disclaimer*: In this example, configuration files are located in `C:/tmp`

> *Disclaimer*: The binding of volume `C:/tmp/application.properties` to `/application/application.properties` is tested using Windows Powershell. Other terminals might run into an error.

The API endpoint documentation is available at:

	http://{host}:{port}/v3/api-docs
	
The Swagger UI for the endpoint is available at:

	http://{host}:{port}/swagger-ui/index.html

It supports DotAAS Part 1 V3 and the following HTTP/REST endpoints defined in [DotAAS Part 2 V3 - Submodel Repository](https://app.swaggerhub.com/apis/Plattform_i40/SubmodelRepositoryServiceSpecification/V3.0.1_SSP-001):

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
  * PutSubmodelElementByPath
  * DeleteSubmodelElementByPath
  * PatchSubmodelElementByPath (including ValueOnly)
  * GetSubmodelElementValueByPath
  * GetFileByPath
  * PutFileByPath
  * DeleteFileByPath

Right now, no additional input parameters modifying the output (e.g., serializationModifier) are supported, if not specified otherwise.

In addition, it supports the following backends:
* InMemory, MongoDB

Furthermore, the following features are provided:
* [MQTT eventing](basyx.submodelrepository-feature-mqtt)
* [Submodel Repository Authorization](basyx.submodelrepository-feature-authorization)
* [Submodel Registry Integration](basyx.submodelrepository-feature-registry-integration)
* [Operation Delegation](basyx.submodelrepository-feature-operation-delegation)

For a configuration example, see [application.properties](basyx.submodelrepository.component/src/main/resources/application.properties)

The Health Endpoint and CORS Documentation can be found [here](../docs/Readme.md). 

## Configure Favicon
To configure the favicon, add the favicon.ico to [basyx-java-server-sdk\basyx.common\basyx.http\src\main\resources\static](../basyx.common/basyx.http/src/main/resources/static/).
