# AssetAdministrationShell Registry - Authorization
This feature enables authorized access to the AssetAdministrationShell Registry.

To enable this feature, the following properties should be configured:

```
basyx.feature.authorization.enabled = true
basyx.feature.authorization.type = <The type of authorization to enable>
basyx.feature.authorization.jwtBearerTokenProvider = <The Jwt token provider>
basyx.feature.authorization.rbac.file = <Class path of the Rbac rules file if authorization type is rbac>
spring.security.oauth2.resourceserver.jwt.issuer-uri= <URI of the resource server>
```

Note: Only Role Based Access Control (RBAC) is supported as authorization type as of now, also Keycloak is the only Jwt token provider supported now, and it is also a default provider. 

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
    "role": "basyx-reader",
    "action": "READ",
    "targetInformation": {
      "@type": "aas-registry",
      "aasId": "*"
    }
  },
  {
    "role": "admin",
    "action": ["CREATE", "READ", "UPDATE", "DELETE"],
    "targetInformation": {
      "@type": "aas-registry",
      "aasId": "*"
    }
  },
  {
    "role": "basyx-deleter",
    "action": "DELETE",
    "targetInformation": {
      "@type": "aas-registry",
      "aasId": "specificAasId"
    }
  }
 ]
```

The role defines which role is allowed to perform the defined actions. The role is as per the configuration of identity providers or based on the organization. Action could be CREATE, READ, UPDATE, DELETE, and EXECUTE, there could be a single action or multiple actions as a list (cf. admin configuration above).

The targetInformation defines coarse-grained control over the resource, you may define the aasId with a wildcard (\*), it means the defined role x with action y can access any Asset Administration Shell Descriptors on the registry. You can also define a specific AAS Identifier in place of the wildcard (\*), then the role x with action y could be performed only on that particular AAS Descriptor.

Note: 
* The Action are fixed as of now and limited to (CREATE, READ, UPDATE, DELETE, and EXECUTE) but later user configurable mapping of these actions would be provided.
* Each rule should be unique in combination of role + action + target information

## Action table for RBAC

Below is a reference table that shows which actions are used in what endpoints of the AAS Registry:

| Action  | Endpoint                                                                                                                                                                                                                                                                                     |
|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| READ    | GET /shell-descriptors <br /> GET /shell-descriptors/{aasIdentifier} <br /> GET /shell-descriptors/{aasIdentifier}/submodel-descriptors <br /> GET /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} <br /> GET /search                                           |
| CREATE  | POST /shell-descriptors <br />                                                                                                                                                                                                                                                               |
| UPDATE  | PUT /shell-descriptors/{aasIdentifier} <br /> PUT /shell-descriptors/{aasIdentifier}/submodel-descriptors <br /> PUT /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} <br /> DELETE /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} |
| DELETE  | DELETE /shell-descriptors/{aasIdentifier}  <br /> DELETE /shell-descriptors                                                                                                                                                                                                                                           |
| EXECUTE | -                                                                                                                                                                                                                                                                                            |


