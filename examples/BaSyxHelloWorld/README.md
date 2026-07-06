# HelloWorld BaSyx Java Example

This tutorial demonstrates how to use **Eclipse BaSyx** to create and manage an **Asset Administration Shell (AAS)** in Java. 

>You can find the HelloWorld Java Example Code here:  
>https://wiki.basyx.org/????????????????????????????

## Overview

In this tutorial you will learn how to:

1. Connect to a BaSyx environment  
2. Create an AAS  
3. Create a Submodel  
4. Add Submodel Elements  
5. Create Operations  
6. Update and read data  
7. Delete elements  
8. Remove the full AAS  

## Lifecycle Summary

>Create AAS  
>↓  
>Create Submodel  
>↓  
>Add Elements  
>↓  
>Add Operations  
>↓  
>Interact / Update  
>↓  
>Invoke Operations  
>↓  
>Delete Elements  
>↓  
>Delete Submodel  
>↓  
>Delete AAS  

 
## Running BaSyx Services

Make sure the following services are running:

- AAS Repository  
- Submodel Repository  
- AAS Registry  
- Submodel Registry  

Default endpoints used:

```java
String aasRegistryBaseUrl = "http://localhost:8082";
String aasRepositoryBaseUrl = "http://localhost:8081";
String submodelRegistryBaseUrl = "http://localhost:8083";
String submodelRepositoryBaseUrl = "http://localhost:8081";
```

BaSyx provides Docker images for quick setup.

 

## Step 1 – BaSyx Docker Setup

Before starting the implementation, make sure your BaSyx environment is running via Docker.

>You can follow the official Quick Installation Guide here:  
>https://wiki.basyx.org/en/latest/content/introduction/quickstart.html

 

## Step 2 – Configure BaSyx Environment

Configure BaSyx by editing the file:

`basyx/aas-env.properties`

Use the following configuration:

```properties
server.port=8081
basyx.backend=InMemory
basyx.environment=file:aas
basyx.cors.allowed-origins=*
basyx.cors.allowed-methods=GET,POST,PATCH,DELETE,PUT,OPTIONS,HEAD
spring.servlet.multipart.max-file-size=500MB
spring.servlet.multipart.max-request-size=500MB
```

 

# Step 3 – BaSyx Library Imports

Add the required BaSyx and AAS4J dependencies:

```java
import org.eclipse.digitaltwin.aas4j.v3.model.*;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.*;
import org.eclipse.digitaltwin.basyx.aasenvironment.client.ConnectedAasManager;
import org.eclipse.digitaltwin.basyx.aasservice.client.ConnectedAasService;
import org.eclipse.digitaltwin.basyx.operation.InvokableOperation;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
```
Edit your `pom.xml` to have this (or newer version) dependencies :

```xml
    <dependencies>
        <dependency>
            <groupId>org.eclipse.digitaltwin.basyx</groupId>
            <artifactId>basyx.aasenvironment-client</artifactId>
            <version>2.0.0-milestone-04</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.digitaltwin.basyx</groupId>
            <artifactId>basyx.aasrepository-client</artifactId>
            <version>2.0.0-milestone-08</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.digitaltwin.basyx</groupId>
            <artifactId>basyx.submodelrepository-client</artifactId>
            <version>2.0.0-milestone-08</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.digitaltwin.basyx</groupId>
            <artifactId>basyx.aasservice-client</artifactId>
            <version>2.0.0-milestone-08</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.digitaltwin.basyx</groupId>
            <artifactId>basyx.submodelservice-client</artifactId>
            <version>2.0.0-milestone-08</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.digitaltwin.aas4j</groupId>
            <artifactId>aas4j-dataformat-aasx</artifactId>
            <version>2.0.1</version>
        </dependency>
    </dependencies>
```

 

## Step 4 – Connect to BaSyx

```java
ConnectedAasManager helloManager =
    new ConnectedAasManager(
        aasRegistryBaseUrl,
        aasRepositoryBaseUrl,
        submodelRegistryBaseUrl,
        submodelRepositoryBaseUrl
    );
```

This manager allows you to:

- Create AAS  
- Create Submodels  
- Delete resources  
- Access services  

 

## Step 5 – Create an AAS

```java
AssetAdministrationShell helloAAS =
    new DefaultAssetAdministrationShell.Builder()
        .id("http://example.com/aas/helloWorld")
        .idShort("HelloWorldAAS")
        .build();
```

 

## Step 6 – Create a Submodel

### Semantic Reference

```java
DefaultKey key = new DefaultKey.Builder()
    .type(KeyTypes.SUBMODEL)
    .value("http://example.com/aas/helloWorld/submodel")
    .build();

DefaultReference ref = new DefaultReference.Builder()
    .type(ReferenceTypes.EXTERNAL_REFERENCE)
    .keys(key)
    .build();
```

### Submodel

```java
Submodel helloSubmodel =
    new DefaultSubmodel.Builder()
        .id("http://example.com/aas/helloWorld/submodel")
        .idShort("helloSubmodel")
        .semanticId(ref)
        .build();
```

 

## Step 7 – Deploy AAS and Submodel

```java
helloManager.createAas(helloAAS);

helloManager.createSubmodelInAas(
    helloAAS.getId(),
    helloSubmodel
);
```

 

## Step 8 – Get Service Access

```java
ConnectedAasService helloAASService =
    helloManager.getAasService(helloAAS.getId());

ConnectedSubmodelService helloSMService =
    helloManager.getSubmodelService(helloSubmodel.getId());
```

 

## Step 9 – Create Submodel Collection

```java
SubmodelElementCollection helloCollection =
    new DefaultSubmodelElementCollection.Builder()
        .idShort("helloSMCollection")
        .value(List.of())
        .build();

helloSMService.createSubmodelElement(helloCollection);
```

 

## Step 10 – Create Operation (Pythagoras Example)

```java
Operation helloOperation =
    new InvokableOperation.Builder()
        .idShort("pythagorasOperation")
        .inputVariables(input_A)
        .inputVariables(input_B)
        .outputVariables(result_C)
        .invokable(Main::pythagoras)
        .build();
```

 

## Step 11 – Add Elements to Submodel

```java
SubmodelElementCollection helloSMC =
    (SubmodelElementCollection)
        helloSMService.getSubmodelElement("helloSMCollection");

helloSMC.getValue().add(
    new DefaultProperty.Builder()
        .idShort("helloProperty")
        .valueType(DataTypeDefXsd.STRING)
        .value("h3ll0World!")
        .build()
);

helloSMC.getValue().add(helloOperation);

helloSMService.updateSubmodelElement(
    "helloSMCollection",
    helloSMC
);
```

 

## Step 12 – Definition of Invoked Operation (Pythagoras)

>The operation calculates:
> `C = sqrt(A² + B²)`

```java
private static OperationVariable[] pythagoras(OperationVariable[] inputs) {
    Property A = (Property) inputs[0].getValue();
    Property B = (Property) inputs[1].getValue();

    Integer iA = Integer.valueOf(A.getValue());
    Integer iB = Integer.valueOf(B.getValue());

    Integer C_out = (int) Math.sqrt(iA*iA + iB*iB);

    Property C = new DefaultProperty.Builder()
        .idShort("C")
        .value(String.valueOf(C_out))
        .build();

    return new OperationVariable[] {
        new DefaultOperationVariable.Builder().value(C).build()
    };
}
```

 

## Step 13 – Delete Property / Submodel / AAS

### Delete Property
```java
helloSMService.deleteSubmodelElement(
    "helloSMCollection.helloProperty"
);
```

 

### Delete Submodel

```java
helloManager.deleteSubmodelOfAas(
    helloAAS.getId(),
    helloSubmodel.getId()
);
```

 

### Delete AAS

```java
helloManager.deleteAas(
    helloAAS.getId()
);
```

 

