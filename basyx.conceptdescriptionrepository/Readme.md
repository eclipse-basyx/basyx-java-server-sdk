# Eclipse BaSyx - ConceptDescription Repository 
Eclipse BaSyx provides the ConceptDescription Repository as off-the-shelf component:

    docker run --name=cd-repo -p:8081:8081 -v C:/tmp/application.properties:/application/application.properties eclipsebasyx/conceptdescription-repository:2.0.0-SNAPSHOT 

> *Disclaimer*: In this example, configuration files are located in `C:/tmp`

> *Disclaimer*: The binding of volume `C:/tmp/application.properties` to `/application/application.properties` is tested using Windows Powershell. Other terminals might run into an error.

The API endpoint documentation is available at:

	http://{host}:{port}/v3/api-docs
	
The Swagger UI for the endpoint is available at:

	http://{host}:{port}/swagger-ui/index.html

It supports DotAAS Part 1 V3 and all HTTP/REST endpoints defined in [DotAAS Part 2 V3 - ConceptDescription Repository](https://app.swaggerhub.com/apis/Plattform_i40/ConceptDescriptionRepositoryServiceSpecification/V3.0.1_SSP-001).

In addition, it supports the following backends:
* InMemory
* MongoDB

Furthermore, the following features are provided:
* [Concept Description Repository Authorization](basyx.conceptdescriptionrepository-feature-authorization)

For a configuration example, see [application.properties](basyx.conceptdescriptionrepository.component/src/main/resources/application.properties)

The Health Endpoint and CORS Documentation can be found [here](../docs/Readme.md). 

Right now, no additional input parameters modifying the output (e.g., cursor, serializationModifier) are supported.

## Configure Favicon
To configure the favicon, add the favicon.ico to [basyx-java-server-sdk\basyx.common\basyx.http\src\main\resources\static](../basyx.common/basyx.http/src/main/resources/static/).
