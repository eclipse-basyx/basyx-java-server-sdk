# Eclipse BaSyx - Standalone Submodel Service Example
This project is a blueprint on how to build a standalone submodel component and provide it as a Docker Image. 

## Content
* ExampleSubmodelFactory: Creates a submodel with Id  _Example_ and two SubmodelElements:
  * Property  _test_  with value  _123_ 
  * Operation  _square_  that takes a property and squares its value
* ExampleSubmodelConfiguration: Contains the spring setup
* ExampleSubmodelComponent: Starts the ExampleSubmodelComponent

## Building as Docker Image
The standalone submodel can be build as docker image via:

```
mvn clean install -Ddocker.namespace=yournamespace
```

For image name configuration, see the  _docker.image.name_  property in [pom.xml](pom.xml).


After successful installation, the container can be access on  _http://localhost:8081/submodel_ after being started via:

```
docker run -p 8081:8081 testnamespace/standalone_submodel_example:2.0.0-SNAPSHOT
```

