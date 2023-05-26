# AssetAdministrationShell Repository - MQTT Eventing
This feature provides hierarchical MQTT eventing for a multitude of events:

| Event       |Topic        | Payload |
| ----------- | ----------- |    ---     |
| AAS Created | /aas-repository/\$repoId/shells/created| Created AAS JSON |
| AAS Updated   | /aas-repository/\$repoId/shells/updated| Updated AAS JSON|
| AAS Deleted   | /aas-repository/\$repoId/shells/deleted| Deleted AAS JSON|
| AssetInformation Set | /aas-repository/\$repoId/shells/\$shellId/assetInformation/updated | Created AssetInformation JSON |
| SubmodelReference Added | /aas-repository/\$repoId/shells/$shellId/submodelReferences/created | Created SubmodelReference JSON |
| SubmodelReference Removed | /aas-repository/\$repoId/shells/\$shellId/submodelReferences/deleted | Deleted SubmodelReference JSON |
