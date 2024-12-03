# Submodel Repository - MQTT Eventing
This feature provides hierarchical MQTT eventing for a multitude of events:

| Event       |Topic        | Payload |
| ----------- | ----------- |    ---     |
| Submodel Created | sm-repository/$repoId/submodels/created| Created SM JSON  |
| Submodel Updated   | sm-repository/$repoId/submodels/updated| Updated SM JSON|
| Submodel Deleted   | sm-repository/$repoId/submodels/deleted| Deleted SM JSON|
| SubmodelElement Created | sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/submodelElements/$idShortPath/created | Created SubmodelElement JSON  |
| SubmodelElement Updated | sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/submodelElements/$idShortPath/updated | Updated SubmodelElement JSON  |
| SubmodelElement Deleted | sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/submodelElements/$idShortPath/deleted | Deleted SubmodelElement JSON  |
| SubmodelElements Patched | sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/submodelElements/patched | Patched SubmodelElements JSON  |
| FileValue Updated | sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/submodelElements/$idShortPath/attachment/updated | Updated SubmodelElement JSON  |
| FileValue Deleted | sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/submodelElements/$idShortPath/attachment/deleted | Deleted SubmodelElement JSON  | 

Per default, the SubmodelElement topic payloads include the SubmodelElement's value. If this is not desired, the SubmodelElement can be annotated with a Qualifier of type *emptyValueUpdateEvent* and value *true* 
