# AssetAdministrationShell Repository - MQTT Eventing
This feature provides hierarchical MQTT eventing for a multitude of events:

| Event       |Topic        | Payload |
| ----------- | ----------- |    ---     |
| AAS Created | aas-repository/\$repoId/shells/created| Created AAS JSON |
| AAS Updated   | aas-repository/\$repoId/shells/updated| Updated AAS JSON|
| AAS Deleted   | aas-repository/\$repoId/shells/deleted| Deleted AAS JSON|
| Submodel Reference Created | aas-repository/\$repoId/shells/submodels/\$submodelIdBase64Url/created| Created AAS JSON |
| Submodel Reference Deleted   | aas-repository/\$repoId/shells/submodels/\$submodelIdBase64Url/deleted| Deleted AAS JSON|

Identifier segments use unpadded UTF-8 Base64URL encoding. Repository names are not encoded.

> **Breaking topic migration:** Submodel-reference events are published only on the encoded canonical topics. Existing subscribers using raw submodel IDs must update their subscriptions.
