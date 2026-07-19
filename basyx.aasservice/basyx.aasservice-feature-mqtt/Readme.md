# AssetAdministrationShell Service - MQTT Eventing
This feature provides hierarchical MQTT eventing for a multitude of events:

| Event       |Topic        | Payload |
| ----------- | ----------- |    ---     |
| AssetInformation Set | aas-repository/\$repoId/shells/\$shellIdBase64Url/assetInformation/updated | Created AssetInformation JSON |
| SubmodelReference Added | aas-repository/\$repoId/shells/\$shellIdBase64Url/submodelReferences/created | Created SubmodelReference JSON |
| SubmodelReference Removed | aas-repository/\$repoId/shells/\$shellIdBase64Url/submodelReferences/deleted | Deleted SubmodelReference JSON |

Identifier segments use unpadded UTF-8 Base64URL encoding. Repository names are not encoded.

> **Breaking topic migration:** AAS service events are published only on the encoded canonical topics. Existing subscribers using raw or percent-encoded shell IDs must update their subscriptions.
