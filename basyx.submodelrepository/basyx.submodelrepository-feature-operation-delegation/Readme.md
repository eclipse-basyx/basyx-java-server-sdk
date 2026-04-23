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

## Security defaults for operation delegation

Operation delegation now validates each outbound delegation URI before dispatch.

- Only `http` and `https` URIs are allowed.
- Loopback, private, link-local, and metadata addresses are blocked by default.
- Redirect responses are rejected.
- Explicit allowlists can be configured for approved hybrid deployments.

Example configuration:

```
basyx.submodelrepository.feature.operation.delegation.security.enabled = true
basyx.submodelrepository.feature.operation.delegation.security.denyPrivateTargets = true
basyx.submodelrepository.feature.operation.delegation.security.denyLinkLocalTargets = true
basyx.submodelrepository.feature.operation.delegation.security.denyLoopbackTargets = true
basyx.submodelrepository.feature.operation.delegation.security.denyMetadataTargets = true
basyx.submodelrepository.feature.operation.delegation.security.denyRedirects = true

# Optional explicit overrides for trusted targets
basyx.submodelrepository.feature.operation.delegation.security.allowlist.hosts = localhost,*.trusted.example
basyx.submodelrepository.feature.operation.delegation.security.allowlist.cidrs = 10.42.0.0/16,127.0.0.0/8,::1/128
basyx.submodelrepository.feature.operation.delegation.security.allowlist.ports = 443,8443
```

If an existing setup delegates to localhost or private network endpoints, configure the corresponding allowlist entries explicitly.
