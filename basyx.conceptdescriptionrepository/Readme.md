# Eclipse BaSyx - ConceptDescription Repository 
Eclipse BaSyx provides the ConceptDescription Repository as off-the-shelf component:

    docker run --name=aas-env -p:8081:8081 -v C:/tmp:/usr/share/config eclipsebasyx/aas-environment:2.0.0-SNAPSHOT 


It supports DotAAS Part 1 V3 and all HTTP/REST endpoints defined in [DotAAS Part 2 V3 - ConceptDescription Repository](https://app.swaggerhub.com/apis/Plattform_i40/ConceptDescriptionRepositoryServiceSpecification/V3.0_SSP-001).
In addition, it supports InMemory as well as MongoDB backends. 

For a configuration example, see [application.properties](basyx.conceptdescriptionrepository.component/src/main/resources/application.properties)
