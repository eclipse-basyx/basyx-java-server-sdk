# Eclipse BaSyx - Submodel Service 
The Submodel Service is intended to provide a standalone Submodel. In contrast to the Submodels hosted in the Submodel Repository, a standalone Submodels enables users to provide custom logic, e.g., invokable operations or proactive behavior (i.e., type 3 AAS). Thus, the Submodel Service is not provided as off-the-shelf component but has to be tailored and build individually.


See [Example Standalone Submodel](basyx.submodelservice.example) for further explanation on how to build the standalone submodel.

The API endpoint documentation is available at:

	http://{host}:{port}/v3/api-docs
	
The Swagger UI for the endpoint is available at:

	http://{host}:{port}/swagger-ui/index.html

It supports DotAAS Part 1 V3 and the following HTTP/REST endpoints defined in [DotAAS Part 2 V3 - Submodel Service](https://app.swaggerhub.com/apis/Plattform_i40/SubmodelServiceSpecification/V3.0.1_SSP-001):

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

The Health Endpoint and CORS Documentation can be found [here](../docs/Readme.md). 
