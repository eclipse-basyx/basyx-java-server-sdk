# Eclipse BaSyx - ConceptDescription Repository 
Eclipse BaSyx provides the ConceptDescription Repository as off-the-shelf component:

    docker run --name=cd-repo -p:8081:8081 -v C:/tmp:/usr/share/config eclipsebasyx/conceptdescription-repository:2.0.0-SNAPSHOT 

The API endpoint documentation is available at:

	http://{host}:{port}/v3/api-docs
	
The Swagger UI for the endpoint is available at:

	http://{host}:{port}/swagger-ui/index.html

It supports DotAAS Part 1 V3 and all HTTP/REST endpoints defined in [DotAAS Part 2 V3 - ConceptDescription Repository](https://app.swaggerhub.com/apis/Plattform_i40/ConceptDescriptionRepositoryServiceSpecification/V3.0_SSP-001).
In addition, it supports InMemory as well as MongoDB backends. 

For a configuration example, see [application.properties](basyx.conceptdescriptionrepository.component/src/main/resources/application.properties)
