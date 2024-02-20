# Aas Environment - Authorization
This feature enables authorized access to the Aas Environment.

To enable this feature, the following properties should be configured:

```
basyx.feature.authorization.enabled = true
basyx.feature.authorization.type = <The type of authorization to enable>
basyx.feature.authorization.jwtBearerTokenProvider = <The Jwt token provider>
basyx.feature.authorization.rbac.file = <Class path of the Rbac rules file if authorization type is rbac>
spring.security.oauth2.resourceserver.jwt.issuer-uri= <URI of the resource server>
```

Note: Only Role Based Access Control (RBAC) is supported as authorization type as of now, also Keycloak is the only Jwt token provider supported now and it is also a default provider. 

To know more about the RBAC, please refer [Authorization Services Guide](https://www.keycloak.org/docs/latest/authorization_services/index.html)
To know more about the Keycloak server administration, please refer [Server Administration Guide](https://www.keycloak.org/docs/latest/server_admin/#keycloak-features-and-concepts)

An example valid configuration:

```
basyx.feature.authorization.enabled = true
basyx.feature.authorization.type = rbac
basyx.feature.authorization.jwtBearerTokenProvider = keycloak
basyx.feature.authorization.rbac.file = classpath:rbac_rules.json
spring.security.oauth2.resourceserver.jwt.issuer-uri= http://localhost:9096/realms/BaSyx
```

## RBAC rule configuration

For configuring RBAC rules, all the rbac rules should be configured inside a json file, the rules are defined as below:

```
[
  {
    "role": "basyx-reader-serialization",
    "action": "READ",
    "targetInformation": {
      "@type": "aas-environment",
      "aasIds": ["shell001", "shell002"],
      "submodelIds": ["7A7104BDAB57E184", "AC69B1CB44F07935"],
      "serializationType": "AASX"
    }
  },
  {
    "role": "admin",
    "action": ["CREATE", "READ", "UPDATE", "DELETE"],
    "targetInformation": {
      "@type": "aas-environment",
      "aasIds": "*",
      "submodelIds": "*",
      "serializationType": "ALL"
    }
  },
  {
    "role": "basyx-reader-serialization-two",
    "action": "READ",
    "targetInformation": {
      "@type": "aas-environment",
      "aasIds": ["shell001", "shell002"],
      "submodelIds": ["7A7104BDAB57E184"],
      "serializationType": "JSON"
    }
  }
 ]
```

The role defines which role is allowed to perform the defined actions. The role is as per the configuration of identity providers or based on the organization. Action could be CREATE, READ, UPDATE, DELETE, and EXECUTE, there could be a single action or multiple actions as a list (cf. admin configuration above).

The targetInformation defines coarse-grained control over the resource, you may define the aasIds and submodelIds with a wildcard (\*), it means the defined role x with action y can perform operations on all the AASs and Submodels. You can also define a specific aasIds and submodelIds in place of the wildcard (\*), then the role x with action y could be performed only on that particular AASs and Submodels. Please note that filtering option is currently not supported so, for serialization requests, if you specify some particular aasIds or submodelIds then serialization request will be denied if there are other AASs or Submodels exists in the environment apart from what configured in the rules. The serializationType could be JSON, AASX, XML, or ALL. 

Note: 
* The Action are fixed as of now and limited to (CREATE, READ, UPDATE, DELETE, and EXECUTE) but later user configurable mapping of these actions would be provided.
* For the serialization related requests there should be defined rules for accessing the AASs/Submodels/Concept Descriptions, as the serialization requires all of these elements. For e.g, a role with serialization is configured for Aas Environment target information but if there is no role for reading the AAS/Submodel/Concept Description then the request will be denied.

## Action table for RBAC

Below is a reference table that shows which actions are used in what endpoints of the AasEnvironment:

| Action  | Endpoint                                                                                                                                                                                                            |
|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| READ    | GET /serialization |
| CREATE  | -                                                                                                                                                                                                |
| UPDATE  | -
| DELETE  | -
| EXECUTE | -                                                                                                                                                                                                                   |

