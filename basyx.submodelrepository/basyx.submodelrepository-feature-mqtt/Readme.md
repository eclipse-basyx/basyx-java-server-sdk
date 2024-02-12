# Submodel Repository - MQTT Eventing
This feature provides hierarchical MQTT eventing for a multitude of events:

| Event       |Topic        | Payload |
| ----------- | ----------- |    ---     |
| Submodel Created | /sm-repository/$repoId/submodels/created| Created SM JSON  |
| Submodel Updated   | /sm-repository/$repoId/submodels/updated| Updated SM JSON|
| Submodel Deleted   | /sm-repository/$repoId/submodels/deleted| Deleted SM JSON|
| SubmodelElement Created | /sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/$idShortPath/created | Created SubmodelElement JSON  |
| SubmodelElement Updated | /sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/$idShortPath/updated | Updated SubmodelElement JSON  |
| SubmodelElement Deleted | /sm-repository/$repoId/submodels/$submodelIdBase64URLEncoded/$idShortPath/deleted | Deleted SubmodelElement JSON  |

Per default, the SubmodelElement topic payloads include the SubmodelElement's value. If this is not desired, the SubmodelElement can be annotated with a Qualifier of type *emptyValueUpdateEvent* and value *true* 
