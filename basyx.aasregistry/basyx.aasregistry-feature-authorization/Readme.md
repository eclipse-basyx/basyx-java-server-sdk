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

This section outlines how RBAC rules are defined, stored, and managed, and provides detailed explanations of the backend persistency options available.

### What is a Rule in RBAC?

A rule in the context of RBAC is a policy that defines the actions a specific role can perform on certain resources. Each rule typically consists of the following components:

* **Role**: The entity (user or group) that the rule applies to, such as `admin` or `basyx-reader`.
* **Action**: The operation permitted by the role, such as `CREATE`, `READ`, `UPDATE`, or `DELETE`.
* **Target Information**: The resource(s) that the action can be performed on, like `aas` (Asset Administration Shell) IDs. The targetInformation defines coarse-grained control over the resource, you may define the aasIds with a wildcard (\*), it means the defined role x with action y can access any Asset Administration Shell on the repository. You can also define a specific AAS Identifier in place of the wildcard (\*), then the role x with action y could be performed only on that particular AAS. There could be a single aasId or multiple aasIds as a list.

### Example of a Simple RBAC Rule
```json
{
  "role": "admin",
  "action": "READ",
  "targetInformation": {
    "@type": "aas-registry",
    "aasIds": "*"
  }
}
```

In this example, the rule grants the admin role permission to perform the READ action on all resources of type aas.

## Persistency Support for RBAC Rules

The AAS Repository supports two backend persistency mechanisms for storing RBAC rules:

### InMemory RBAC Storage

* InMemory RBAC stores all rules directly in the application's memory.
* The rules must be configured during the application startup, and they remain in memory for the duration of the application's runtime.
* Rules are defined using a JSON format as defined below.

```json
[
  {
    "role": "basyx-reader",
    "action": "READ",
    "targetInformation": {
      "@type": "aas-registry",
      "aasIds": "*"
    }
  },
  {
    "role": "admin",
    "action": ["CREATE", "READ", "UPDATE", "DELETE"],
    "targetInformation": {
      "@type": "aas-registry",
      "aasIds": "*"
    }
  },
  {
    "role": "basyx-deleter",
    "action": "DELETE",
    "targetInformation": {
      "@type": "aas-registry",
      "aasIds": ["testAasId1", "specificAasId", "testAasId2"]
    }
  }
]
```

[!Note] 
* The Action are fixed as of now and limited to (CREATE, READ, UPDATE, DELETE, and EXECUTE).
* Rules cannot be modified after the application has started.
* This is suitable for simple, small-scale infrastructure or testing environment where the rule set remains static.

#### How to enable this storage?

The InMemory rule storage is used by default, but to explicitly configure, below propertiy needs to be configured inorder to enable the InMemory Storage:

```
basyx.feature.authorization.rules.backend=InMemory
```

### Submodel-based RBAC Storage

* RBAC rules are stored in a dedicated Security Submodel within a Configuration-Submodel Repository.
* The Configuration-Submodel Repository is a general-purpose repository that supports various configuration models, with RBAC rules being part of its Security Submodel.
* This repository is equipped with Authorization to ensure that only designated entities (like administrators or maintainers) can manage the rules inside the Security Submodel.
* Similar to InMemory RBAC storage, initial rules can be defined in JSON format. However, upon application startup, these rules are automatically adapted and stored in the Security Submodel.
* This allows for a more flexible and persistent management of rules, which can be updated or extended.

#### Example Configuration Process:
* Define initial rules in JSON format. (Not mandatory)
* On application startup, these rules are validated and stored in the Security Submodel.

The below JSON RBAC rule is automatically adapted to Submodel-based RBAC rule:

```json
{
  "role": "admin",
  "action": "DELETE",
  "targetInformation": {
    "@type": "aas-registry",
    "aasIds": "*"
  }
}
``` 

The equivalent of above rule in Submodel-based RBAC rule (below in JSON serialized format):
```json
{
  "modelType": "SubmodelElementCollection",
  "idShort": "YWRtaW5ERUxFVEVvcmcuZWNsaXBzZS5kaWdpdGFsdHdpbi5iYXN5eC5hYXNyZXBvc2l0b3J5LmZlYXR1cmUuYXV0aG9yaXphdGlvbi5BYXNUYXJnZXRJbmZvcm1hdGlvbg==",
  "value": [
      {
          "modelType": "Property",
          "value": "admin",
          "idShort": "role"
      },
      {
          "modelType": "SubmodelElementList",
          "idShort": "action",
          "orderRelevant": true,
          "value": [
              {
                  "modelType": "Property",
                  "value": "DELETE"
              }
          ]
      },
      {
          "modelType": "SubmodelElementCollection",
          "idShort": "targetInformation",
          "value": [
              {
                  "modelType": "SubmodelElementList",
                  "idShort": "aasIds",
                  "orderRelevant": true,
                  "value": [
                      {
                          "modelType": "Property",
                          "value": "*"
                      }
                  ]
              }
          ]
      }
  ]
}
```

[!Note] 
* The API for adding and removing rules is consistent with that of a standard Submodel Repository.
* The IdShort of the rule is automatically generated and it will replace the original IdShort configured while adding the rule.
* Only addition and removal of rules are supported; updating existing rules is not allowed due to [constraints](#constraints-and-rule-management).
* Only the responsible entity (typically an administrator or maintainer) should be permitted to manage the rules within the Security Submodel.
* This ensures that the RBAC policies are strictly controlled and secure.

#### How to enable this storage?

The following properties needs to be configured inorder to enable the Submodel-based Storage:

```
basyx.feature.authorization.rules.backend=Submodel
basyx.feature.authorization.rules.backend.submodel.authorization.endpoint=<Endpoint of the Security Submodel>
basyx.feature.authorization.rules.backend.submodel.authorization.token-endpoint=<Token Endpoint>
basyx.feature.authorization.rules.backend.submodel.authorization.grant-type = <CLIENT_CREDENTIALS> or <PASSWORD>
basyx.feature.authorization.rules.backend.submodel.authorization.client-id=<client-id>
basyx.feature.authorization.rules.backend.submodel.authorization.client-secret=<client-id>
basyx.feature.authorization.rules.backend.submodel.authorization.username=<username>
basyx.feature.authorization.rules.backend.submodel.authorization.password=<password>
```

## Constraints and Rule Management

* To ensure quick access to rules, a hash key is generated based on the role, single action, and target information type.
* Duplicate entries with the same role, action, and target information type are not allowed.
* This necessitates the splitting of rules and prevents the update of existing rules (since modifying a rule would alter the hash key, which could impact performance).
* Due to the constraint on hash key generation, updates to existing rules are not supported.
* If a rule needs to be changed, the existing rule must be removed, and a new rule must be added with the updated information.
* Applies to both the storage backend.

### Automatic Rule Splitting

* If a rule specifies multiple actions (e.g., ["CREATE", "READ"]), it will automatically be split into individual rules, each with a single action.

Example of Automatic Splitting:

```json
{
  "role": "admin",
  "action": ["CREATE", "READ"],
  "targetInformation": {
    "@type": "aas-registry",
    "aasIds": "*"
  }
}
```

Will be split into:

```json
{
  "role": "admin",
  "action": "CREATE",
  "targetInformation": {
    "@type": "aas-registry",
    "aasIds": "*"
  }
},
{
  "role": "admin",
  "action": "READ",
  "targetInformation": {
    "@type": "aas-registry",
    "aasIds": "*"
  }
}
```

## Action table for RBAC

Below is a reference table that shows which actions are used in what endpoints of the AAS Registry:

| Action  | Endpoint                                                                                                                                                                                                                                                                                     |
|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| READ    | GET /shell-descriptors <br /> GET /shell-descriptors/{aasIdentifier} <br /> GET /shell-descriptors/{aasIdentifier}/submodel-descriptors <br /> GET /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} <br /> GET /search                                           |
| CREATE  | POST /shell-descriptors <br />                                                                                                                                                                                                                                                               |
| UPDATE  | PUT /shell-descriptors/{aasIdentifier} <br /> PUT /shell-descriptors/{aasIdentifier}/submodel-descriptors <br /> PUT /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} <br /> DELETE /shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} |
| DELETE  | DELETE /shell-descriptors/{aasIdentifier}  <br /> DELETE /shell-descriptors                                                                                                                                                                                                                                           |
| EXECUTE | -                                                                                                                                                                                                                                                                                            |


