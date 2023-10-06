# Components
The components 
* [AAS Repository](../basyx.aasrepository/)
* [Submodel Repository](../basyx.submodelrepository/)
* [Concept Description Repository](../basyx.conceptdescriptionrepository/)
* [AAS Environment](../basyx.aasenvironment/)

offer common mechanisms to check the current health status of the application and configure Cross-Origin Resource Sharing (CORS) options.

## Health Endpoint
The health endpoint to check whether the server is up and running is available at:

	http://{host}:{port}/actuator/health
For a detailed documentation on its configuration possibilities, see [Spring Management Endpoint](Management_Endpoint.md) documentation.

## CORS configuration
Cross-Site Resource Sharing options can be configured under [application.properties](../basyx.aasrepository/basyx.aasrepository.component/src/main/resources/application.properties) with the following attribute:

* Allowed Origin Patterns:<br>
Comma-separated list of origin patterns to allow. Unlike allowed origins which only supports '*', origin patterns are more flexible (for example 'https://*.example.com') and can be used when credentials are allowed. When no allowed origin patterns or allowed origins are set, CORS support is disabled.
  ```
  basyx.cors.allowed-origins=http://localhost:8080, https://*.example.com
  ```
