# Submodel Service - Feature: Operation Dispatching

This feature enables the dispatching of operation calls to registered Java classes. Requests are delegated to the specified classes, allowing for flexible execution of operations.

Additionally, runtime Java code compilation is supported. If a source directory is provided, Java classes can be compiled on the fly. These compiled classes can then be utilized by assigning an `idShort` to a corresponding Java class.

The following configuration properties are available:

| Property                                                                                     | Example                              | Description                                                                                                                                      |
|---------------------------------------------------------------------------------------------|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `basyx.submodelservice.feature.operation.dispatcher.enabled`                                | `true`                               | Determines whether the feature is enabled or not.                                                                                                 |
| `basyx.submodelservice.feature.operation.dispatcher.mappings[SquareOperation]`              | `org.example.SquareOp`               | Maps the `idShortPath` of an operation to the class `org.example.SquareOp`.                                                                       |
| `basyx.submodelservice.feature.operation.dispatcher.mappings[BasicOperations.AddOperation]` | `org.basic.AddOperation`             | Another example of mapping an operation to a Java class.                                                                                          |
| `basyx.submodelservice.feature.operation.dispatcher.defaultMapping`                         | `org.example.MyOperation`            | Specifies the default operation to be called if no specific mapping is found.                                                                     |
| `basyx.submodelservice.feature.operation.dispatcher.java.sources`                           | `sources`                            | Directory containing the Java source files to be compiled. These classes can be dynamically loaded and executed at runtime.                       |
| `basyx.submodelservice.feature.operation.dispatcher.java.classes`                           | `classes`                            | Directory for storing compiled classes. This is used in the classpath when loading classes.                                                       |
| `basyx.submodelservice.feature.operation.dispatcher.java.additionalClasspath`               | `jars/HelloWorld.jar,jars/test.jar`  | Comma-separated list of additional libraries used during source compilation and class loading.                                                    |

