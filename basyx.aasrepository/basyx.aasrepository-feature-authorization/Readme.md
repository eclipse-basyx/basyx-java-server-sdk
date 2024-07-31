# AssetAdministrationShell Repository - Authorization
This feature enables authorized access to the AssetAdministrationShell Repository.

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
    "role": "basyx-reader",
    "action": "READ",
    "targetInformation": {
      "@type": "aas",
      "aasIds": "*"
    }
  },
  {
    "role": "admin",
    "action": ["CREATE", "READ", "UPDATE", "DELETE"],
    "targetInformation": {
      "@type": "aas",
      "aasIds": "*"
    }
  },
  {
    "role": "basyx-deleter",
    "action": "DELETE",
    "targetInformation": {
      "@type": "aas",
      "aasIds": ["testAasId1", "specificAasId", "testAasId2"]
    }
  }
 ]
```

The role defines which role is allowed to perform the defined actions. The role is as per the configuration of identity providers or based on the organization. Action could be CREATE, READ, UPDATE, DELETE, and EXECUTE, there could be a single action or multiple actions as a list (cf. admin configuration above).

The targetInformation defines coarse-grained control over the resource, you may define the aasIds with a wildcard (\*), it means the defined role x with action y can access any Asset Administration Shell on the repository. You can also define a specific AAS Identifier in place of the wildcard (\*), then the role x with action y could be performed only on that particular AAS. There could be a single aasId or multiple aasIds as a list (cf. basyx-deleter above). 

Note: 
* The Action are fixed as of now and limited to (CREATE, READ, UPDATE, DELETE, and EXECUTE) but later user configurable mapping of these actions would be provided.
* Each rule should be unique in combination of role + action + target information

## Action table for RBAC

Below is a reference table that shows which actions are used in what endpoints of the AAS Repository: 

| Action  | Endpoint                                                                                                                                                                                                            |
|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| READ    | GET /shells <br /> GET /shells/{aasIdentifier} <br /> GET /shells/{aasIdentifier}/submodel-refs <br /> GET /shells/{aasIdentifier}/asset-information <br /> GET /shells/{aasIdentifier}/asset-information/thumbnail |
| CREATE  | POST /shells <br />                                                                                                                                                                                                 |
| UPDATE  | PUT /shells/{aasIdentifier} <br /> POST /shells/{aasIdentifier}/submodel-refs <br /> PUT /shells/{aasIdentifier}/asset-information <br /> PUT /shells/{aasIdentifier}/asset-information/thumbnail  <br /> DELETE /shells/{aasIdentifier}/submodel-refs/{submodelIdentifier} <br /> DELETE /shells/{aasIdentifier}/asset-information/thumbnail                 |
| DELETE  | DELETE /shells/{aasIdentifier}           |
| EXECUTE | -                                                                                                                                                                                                                   |

