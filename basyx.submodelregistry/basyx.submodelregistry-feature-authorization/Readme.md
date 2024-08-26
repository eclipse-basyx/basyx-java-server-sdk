# Submodel Registry - Authorization
This feature enables authorized access to the Submodel Registry.

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
      "@type": "submodel-registry",
      "submodelId": "*"
    }
  },
  {
    "role": "admin",
    "action": ["CREATE", "READ", "UPDATE", "DELETE"],
    "targetInformation": {
      "@type": "submodel-registry",
      "submodelId": "*"
    }
  },
  {
    "role": "basyx-deleter",
    "action": "DELETE",
    "targetInformation": {
      "@type": "submodel-registry",
      "submodelId": "specificSubmodelId"
    }
  }
 ]
```

The role defines which role is allowed to perform the defined actions. The role is as per the configuration of identity providers or based on the organization. Action could be CREATE, READ, UPDATE, DELETE, and EXECUTE, there could be a single action or multiple actions as a list (cf. admin configuration above).

The targetInformation defines coarse-grained control over the resource, you may define the submodelId with a wildcard (\*), it means the defined role x with action y can access any Submodel Descriptors on the registry. You can also define a specific Submodel Identifier in place of the wildcard (\*), then the role x with action y could be performed only on that particular Submodel Descriptor.

Note:
* The Action are fixed as of now and limited to (CREATE, READ, UPDATE, DELETE, and EXECUTE) but later user configurable mapping of these actions would be provided.
* Each rule should be unique in combination of role + action + target information

## Action table for RBAC

Below is a reference table that shows which actions are used in what endpoints of the Submodel Registry:

| Action  | Endpoint                                                                                                                                                                                                                                                                          |
|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| READ    | GET /submodel-descriptors <br /> GET /submodel-descriptors/{submodelIdentifier}                          |
| CREATE  | POST /submodel-descriptors <br />                                                                                                                                                                                                                                                    |
| UPDATE  | PUT /submodel-descriptors/{submodelIdentifier} |
| DELETE  | DELETE /submodel-descriptors/{submodelIdentifier}  <br /> DELETE /submodel-descriptors                                                                                                                                                                                                      |
| EXECUTE | -                                                                                                                                                                                                                                                                                 |


