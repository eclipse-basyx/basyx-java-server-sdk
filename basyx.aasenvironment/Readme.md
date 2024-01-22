# Eclipse BaSyx - AAS Environment
Eclipse BaSyx provides the AAS Environment as off-the-shelf component:

    docker run --name=aas-env -p:8081:8081 -v C:/tmp/application.properties:/application/application.properties eclipsebasyx/aas-environment:2.0.0-SNAPSHOT 

> *Disclaimer*: In this example, configuration files are located in `C:/tmp`

> *Disclaimer*: The binding of volume `C:/tmp/application.properties` to `/application/application.properties` is tested using Windows Powershell. Other terminals might run into an error.

It aggregates the AAS Repository, Submodel Repository and ConceptDescription Repository into a single component. For its features and configuration, see the documentation of the respective components.

In addition, it supports the following endpoint defined in DotAAS Part 2 V3 - Serialization Interface:
- GenerateSerializationByIds

The Aggregated API endpoint documentation is available at:

	http://{host}:{port}/v3/api-docs
	
The Aggregated Swagger UI for the endpoint is available at:

	http://{host}:{port}/swagger-ui/index.html

For a configuration example, see [application.properties](./basyx.aasenvironment.component/src/main/resources/application.properties)
The Health Endpoint and CORS Documentation can be found [here](../docs/Readme.md). 

## Preconfiguration of AAS Environments
The AAS Environment Component supports the preconfiguration of AAS Environments (e.g., XML, JSON, AASX) via the _basyx.environment_ parameter. 

The feature supports both preconfiguring explicit files (e.g., file:myDevice.aasx) as well as directories (e.g., file:myDirectory) that will be recursively scanned for serialized environments.

Please note that collision of ids of Submodels and AAS in the preconfigured environments will lead to an error. For ConceptDescriptions, however, id collisions are ignored since they are assumed to be identical. Thus, only the first occurance of a ConceptDescription with the same Id will be uploaded. Further ConceptDescriptions with the same Id will only lead to a warning in the log. 

Furthermore, if Identifiables (AAS, Submodels, ConceptDescriptions) are already existing in the repositories before adding the preconfigured environments (e.g., due to using MongoDB persistency and restarting the server), the _Version_ & _Revision_ (cf. AdministrativeInformation) are leveraged for determining if the existing Identifiables should be overwritten. The following examples illustrate this behavior:
* Preconfigured Identifiable has same version and same revision in comparison to the already existing => No overwriting
* Preconfigured Identifiable has older version or same version and older revision in comparison to the already existing => No overwriting
* Preconfigured Identifiable has newer version or same version but newer revision in comparison to the already existing => Server version is overwritten


For examples, see [application.properties](./basyx.aasenvironment.component/src/main/resources/application.properties)
