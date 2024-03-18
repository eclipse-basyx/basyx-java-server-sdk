# AssetAdministrationShell Service - MQTT Eventing
This feature provides hierarchical MQTT eventing for a multitude of events:

| Event       |Topic        | Payload |
| ----------- | ----------- |    ---     |
| AssetInformation Set | aas-repository/\$repoId/shells/\$shellId/assetInformation/updated | Created AssetInformation JSON |
| SubmodelReference Added | aas-repository/\$repoId/shells/$shellId/submodelReferences/created | Created SubmodelReference JSON |
| SubmodelReference Removed | aas-repository/\$repoId/shells/\$shellId/submodelReferences/deleted | Deleted SubmodelReference JSON |
