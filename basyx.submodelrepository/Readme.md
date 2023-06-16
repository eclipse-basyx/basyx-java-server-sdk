# Eclipse BaSyx - Submodel Repository 
Eclipse BaSyx provides the Submodel Repository as off-the-shelf component:

    docker run --name=sm-repo -p:8081:8081 -v C:/tmp:/usr/share/config eclipsebasyx/submodel-repository:2.0.0-SNAPSHOT 

The API endpoint documentation is available at:

	http://{host}:{port}/v3/api-docs
	
The Swagger UI for the endpoint is available at:

	http://{host}:{port}/swagger-ui/index.html

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
* InMemory, MongoDB

Furthermore, the following features are provided:
* [MQTT eventing](basyx.submodelrepository-feature-mqtt)

The health Endpoint to dertermine to check weather the server is up and running is available at:

	http://{host}:{port}/health

For a configuration example, see [application.properties](basyx.submodelrepository.component/src/main/resources/application.properties)

Cross-Site Resource Sharing (CORS) is enabled by setting the `management.endpoints.web.cors.allowed-origins` property.

The health Endpoint to dertermine to check weather the server is up and running is available at:

	http://{host}:{port}/health

Cross-Site Resource Sharing options can be configured under [application.properties](.basyx.submodelrepository.component/src/main/resources/application.properties) with the following attributes: (As seen on https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.cors)

* Allowed Origins:<br>
Comma-separated list of origin patterns to allow. Unlike allowed origins which only supports '*', origin patterns are more flexible (for example 'https://*.example.com') and can be used when credentials are allowed. When no allowed origin patterns or allowed origins are set, CORS support is disabled.
  ```
  management.endpoints.web.cors.allowed-origins=https://example.com
  ```
* Allowed Origin Patterns:<br>
Comma-separated list of origin patterns to allow. Unlike allowed origins which only supports '*', origin patterns are more flexible (for example 'https://*.example.com') and can be used when credentials are allowed. When no allowed origin patterns or allowed origins are set, CORS support is disabled.
  ```
  management.endpoints.web.cors.allowed-origin-patterns=https://*.example.com
  ```
* Allowed Methods:<br>
Comma-separated list of methods to allow. '*' allows all methods. When not set,defaults to GET.
  ```
  management.endpoints.web.cors.allowed-methods=
  ```
* Allowed Headers:<br>
Comma-separated list of headers to allow in a request. '*' allows all headers.
  ```
  management.endpoints.web.cors.allowed-headers=
  ```
* Exposed Headers:<br>
Comma-separated list of headers to include in a response.
  ```
  management.endpoints.web.cors.exposed-headers=
  ```

* Allow Credentials: <br>
Boolean, whether credentials are supported. When not set, credentials are not supported.
  ```
  management.endpoints.web.cors.allow-credentials=
  ```
* Max Age:<br>
Number, how long in seconds the response from a pre-flight request can be cached by clients.
  ```
  management.endpoints.web.cors.max-age=
  ```

Right now, no additional input parameters modifying the output (e.g., cursor, serializationModifier) are supported.
