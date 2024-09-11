# Eclipse BaSyx - Standalone Submodel Service Component

This project provides a generic Submodel Service component. With this component, you can deploy your own Submodel Services without the need to build a Spring Boot application or create and deploy your own Dockerfiles.

*Invoke* calls to the Submodel element *Operation* are delegated in the generic component to Java classes that can either be precompiled or provided as source code.

These methods can be provided as simple Java classes and do not require additional dependencies for the project. At runtime, the classes from the *aas4j-model* module are already available. Additional libraries can be included via an environment variable.

## Configuration

The [example folder](./example/) contains sample settings. For assigning `idShortPaths` to executable Java classes, configuration should be done using Properties in the form of a Properties file or YAML. Environment variables are not processed correctly by Spring when mapping.

When running the container, the execution folder is /application. 

**Performance Note**: The startup time of the service can be significantly reduced if the sources are already compiled and placed as JAR files or class files in the /application directory. Pre-compiling your code before building the Docker image can improve performance and reduce initialization time.

### System Properties

Below are the individual properties used when starting the application. Except for `basyx.submodel.file`, all properties are optional.

| Property                                              | Example                  | Explanation                                                                                                                       |
|-------------------------------------------------------|--------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `basyx.submodel.file`                                | `submodel.json`          | Path to the file describing the Submodel.                                                                                        |
| `basyx.operation.java.sourcesPath`                   | `sources`                | Source directory where the Java classes to be compiled are located. These classes can be specified to be loaded at runtime to perform operations. |
| `basyx.operation.java.classesPath`                   | `classes`                | Target directory for compilation. Used in the classpath when loading classes.                                                     |
| `basyx.operation.java.additionalClasspath`           | `jars/HelloWorld.jar,jars/` | Comma-separated list of additional libraries used during source compilation and loading of executable classes.                    |
| `basyx.operation.invokation.mappings[SquareOperation]` | `org.example.SquareOp`   | Example of a mapping assignment. The `idShortPath` of an operation is assigned to the class `org.example.SquareOp`.               |
| `basyx.operation.invokation.mappings[BasicOperations.AddOperation]` | `org.basic.AddOperation` | Another example of mapping an operation to a Java class.                                                                          |
| `basyx.operation.invokation.defaultMapping`           | `org.example.MyOperation` | Specifies the operation to be called if no mapping is found.                                                                      |

The example project contains an [application.yml](./example/application.yml) that demonstrates how configuration can be specified clearly using YAML.

### Structure of Java Classes

The Java classes that execute operations do not require dependencies and do not need to extend interfaces or implement classes. It is important that they include a method with the following signature:

```java
public OperationVariable[] invoke(String path, Operation op, OperationVariable[] in)
```

Alternatively, the following method can be used if not all arguments are needed:

```java
public OperationVariable[] invoke(OperationVariable[] in)
```

Here is a simple example:

```java
package org.basic;

import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Operation;
import org.eclipse.digitaltwin.aas4j.v3.model.OperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultOperationVariable;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;

public class AddOperation {

    public OperationVariable[] invoke(String path, Operation op, OperationVariable[] in)  {
        Property first = (Property) in[0].getValue();
        Property second = (Property) in[1].getValue();
        int iFirst = Integer.parseInt(first.getValue());
        int iSecond = Integer.parseInt(second.getValue());
        int result = iFirst + iSecond;
        Property prop = new DefaultProperty.Builder()
                            .value(String.valueOf(result))
                            .valueType(DataTypeDefXsd.INT)
                            .build();
        return new OperationVariable[] { new DefaultOperationVariable.Builder().value(prop).build() };
    }
}
```

The execution is stateless. A new instance is created each time.

### Creating a Custom Image

This setup allows for quick deployment of Submodel Services. However, if you want to avoid configuration and Docker volume binding, you can also create your own images quickly:

```dockerfile
FROM eclipsebasyx/submodel-service:0.2.0-SNAPSHOT
COPY sources/ /application/sources
COPY jars/ /application/jars
COPY submodel.json /application/submodel.json
COPY application.yml /application/config/application.yml
```

The operations can also be precompiled and placed as JARs in the `/application/jars` folder, which is referenced in the `application.yml`. Alternatively, you can place Java classes of the correct syntax in the source folder and reference them in the properties via a mapping.

