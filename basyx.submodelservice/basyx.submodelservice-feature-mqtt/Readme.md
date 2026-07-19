# Submodel Service - MQTT Eventing

This feature provides hierarchical MQTT eventing for submodel-element changes:

| Event | Topic | Payload |
| --- | --- | --- |
| SubmodelElement Created | sm-service/submodels/$submodelIdBase64Url/submodelElements/$idShortPath/created | Created SubmodelElement JSON |
| SubmodelElement Replaced | sm-service/submodels/$submodelIdBase64Url/submodelElements/$idShortPath/updated | Updated SubmodelElement JSON |
| SubmodelElement Value Updated | sm-service/submodels/$submodelIdBase64Url/submodelElements/$idShortPath/value/updated | Updated SubmodelElement JSON |
| SubmodelElement Deleted | sm-service/submodels/$submodelIdBase64Url/submodelElements/$idShortPath/deleted | Deleted SubmodelElement JSON |
| SubmodelElements Patched | sm-service/submodels/$submodelIdBase64Url/submodelElements/patched | Patched SubmodelElements JSON |
| FileValue Updated | sm-service/submodels/$submodelIdBase64Url/submodelElements/$idShortPath/attachment/updated | Updated SubmodelElement JSON |
| FileValue Deleted | sm-service/submodels/$submodelIdBase64Url/submodelElements/$idShortPath/attachment/deleted | Deleted SubmodelElement JSON |

Submodel identifier segments use unpadded UTF-8 Base64URL encoding. idShort paths are not encoded.

By default, SubmodelElement payloads include the element value. To omit it, annotate the element with a qualifier of type `emptyValueUpdateEvent` and value `true`.

> **Breaking topic migration:** The canonical topic now contains the encoded submodel ID, and `$value` PATCH events use `value/updated`. Events are not dual-published on legacy topics, so existing subscribers must update their subscriptions.
