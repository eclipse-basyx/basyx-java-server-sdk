# Submodel Repository - Operation Delegation
An operation is a specific type of submodel element capable of performing server-side functions. However, in certain environments, this is not possible. Therefore, the operation can be delegated to an independent destination (such as an HTTP URL) to be executed. An example request body is given below.

```
[
    {
        "value": {
            "modelType": "Property",
            "value": "5",
            "valueType": "xs:int",
            "idShort": "int"
        }
    }
]
```

This feature is enabled by default and to disable this the following property should be defined:

```
basyx.submodelrepository.feature.operation.delegation.enabled = false
```

To create an operation delegation, a Submodel that has an Operation(-SubmodelElement) must be present:
-	To achieve the delegation functionality, a Qualifier of type "invocationDelegation" is added to the operation.
-	As a value, the connection information (such as an HTTP URL) to the desired operation must be given.

This means that operations can be delegated to endpoints of the same server as well as to external servers. For the front-end, it remains completely transparent whether an operation was called directly or delegated.


The independent destination where the request is to be delegated should support the request with a parameter ([OperationVariable[]](https://github.com/eclipse-aas4j/aas4j/blob/2abf04bc01f80bceafa575cf85da429d5fe63918/model/src/main/java/org/eclipse/digitaltwin/aas4j/v3/model/OperationVariable.java#L31)) and provide the output in a strict format ([OperationVariable[]](https://github.com/eclipse-aas4j/aas4j/blob/2abf04bc01f80bceafa575cf85da429d5fe63918/model/src/main/java/org/eclipse/digitaltwin/aas4j/v3/model/OperationVariable.java#L31))

As of now, only delegation to HTTP URLs is supported.
