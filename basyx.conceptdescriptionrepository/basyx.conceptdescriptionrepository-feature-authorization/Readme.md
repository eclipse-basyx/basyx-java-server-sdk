# Concept Description Repository - Authorization
This feature enables authorized access to the Concept Description Repository.

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
      "@type": "concept-description",
      "conceptDescriptionIds": "*"
    }
  },
  {
    "role": "admin",
    "action": ["CREATE", "READ", "UPDATE", "DELETE"],
    "targetInformation": {
      "@type": "concept-description",
      "conceptDescriptionIds": "*"
    }
  },
  {
    "role": "basyx-deleter",
    "action": "DELETE",
    "targetInformation": {
      "@type": "concept-description",
      "conceptDescriptionIds": "*"
    }
  },
  {
    "role": "basyx-reader-two",
    "action": "READ",
    "targetInformation": {
      "@type": "concept-description",
      "conceptDescriptionIds": ["testCDId1","specificConceptDescriptionId","testCDId2"]
    }
  }
  }
 ]
```

The role defines which role is allowed to perform the defined actions. The role is as per the configuration of identity providers or based on the organization. Action could be CREATE, READ, UPDATE, DELETE, and EXECUTE, there could be a single action or multiple actions as a list (cf. admin configuration above).

The targetInformation defines coarse-grained control over the resource, you may define the conceptDescriptionIds with a wildcard (\*), it means the defined role x with action y can access any Concept Description on the repository. You can also define a specific Concept Description Identifier in place of the wildcard (\*), then the role x with action y could be performed only on that particular Concept Description.
There could be a single conceptDescriptionId or multiple conceptDescriptionIds as a list (cf. basyx-reader-two above).

Note: 
* The Action are fixed as of now and limited to (CREATE, READ, UPDATE, DELETE, and EXECUTE) but later user configurable mapping of these actions would be provided.
* Each rule should be unique in combination of role + action + target information

## Action table for RBAC

Below is a reference table that shows which actions are used in what endpoints of the Concept Description Repository:

| Action  | Endpoint                                                                                                                                                                                                            |
|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| READ    | GET /concept-descriptions <br /> GET /concept-descriptions/{cdIdentifier} |
| CREATE  | POST /concept-descriptions <br />                                                                                                                                                                                                 |
| UPDATE  | PUT /concept-descriptions/{cdIdentifier} |
| DELETE  | DELETE /concept-descriptions/{cdIdentifier}  |
| EXECUTE | -                                                                                                                                                                                                                   |

