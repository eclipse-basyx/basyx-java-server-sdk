# Asset Administration Shell Registry

This is a Java-based implementation of the Asset Administration Shell Registry server and client based on the corresponding [Open-API specification](https://app.swaggerhub.com/apis/Plattform_i40/AssetAdministrationShellRegistryServiceSpecification/V3.0_SSP-001) of the German Plattform Industrie 4.0 and its specification document [Details of the Asset Administration Shell, Part 2](https://industrialdigitaltwin.org/wp-content/uploads/2023/04/IDTA-01002-3-0_SpecificationAssetAdministrationShell_Part2_API.pdf)

[basyx.aasregistry-client-native](basyx.aasregistry-client-native/README.md) can be used to interact with the backend to register or unregister descriptors and submodels or perform search operations.

[basyx.aasregistry-paths](basyx.aasregistry-paths/README.md) generates a builder class that can be used by the registry client to create search requests.

[basyx.aasregistry-plugins](basyx.aasregistry-plugins/README.md) contains maven plugins used by the other projects. 

[basyx.aasregistry-service](basyx.aasregistry-service/README.md) provides the application server to access the AAS descriptor storage and offers an API for REST-based communication.

[basyx.aasregistry-service-basemodel](basyx.aasregistry-service-basemodel/README.md) provides a base model implementation that should be used if you do not need specific model annotations for your storage. It is used for the in-memory storage implementation and you need to add it explicitly as dependency for your server deployment as it is defined as 'provided' dependency in the [basyx.aasregistry-service](basyx.aasregistry-service/README.md) POM.

[basyx.aasregistry-service-basetests](basyx.aasregistry-service-basetests/README.md) provides helper classes and abstract test classes that can be extended in storage tests or integration tests. The abstract test classes already define test methods so that you will get a good test coverage without writing any additional test cases.

[basyx.aasregistry-service-mongodb-storage](basyx.aasregistry-service-mongodb-storage/README.md) provides a registry-storage implementation based on mongoDB that could be used as storage for [aasregistry-service](basyx.aasregistry-service/README.md). It comes with java-based model classes, annotated with mongoDB annotations.

[basyx.aasregistry-service-inmemory-storage](basyx.aasregistry-service-inmemory-storage/README.md) provides a non-persistent registry-storage implementation where instances are stored in hash maps. It can be used as storage for [aasregistry-service](basyx.aasregistry-service/README.md).

[basyx.aasregistry-service-kafka-events](basyx.aasregistry-service-kafka-events/README.md) extends basyx.aasregistry-service with a registry-event-sink implementation that delivers shell descriptor and submodel registration events using Apache Kafka. The default provided by aasregistry-service just logs the events.

[basyx.aasregistry-service-release-kafka-mongodb](basyx.aasregistry-service-release-kafka-mongodb/README.md) is used to combine the server artifacts to a release image that uses [Apache Kafka](https://kafka.apache.org/) as event sink and [MongoDB](https://www.mongodb.com/) as storage.

[basyx.aasregistry-service-release-kafka-mem](basyx.aasregistry-service-release-kafka-mem/README.md) is used to combine the server artifacts to a release image that uses Apache Kafka as event sink and an in-memory storage.

[basyx.aasregistry-service-release-log-mongodb](basyx.aasregistry-service-release-log-mongodb/README.md) is used to combine the server artifacts to a release image that logs registry events and uses MongoDB as data storage.

[basyx.aasregistry-service-release-log-mem](basyx.aasregistry-service-release-log-mem/README.md) is used to combine the server artifacts to a release image that logs registry events and an in-memory storage.

A docker-compose file that illustrates the setup can be found in the [docker-compose](docker-compose/docker-compose.yml) folder.


# Important

The REST API and the client implementation will not be modified - if not a SNAPSHOT version - until a new major version is released or an update of the openAPI definition. All server-side classes and the plugins are not intended to be used as programming library. They could be updated or removed then a new minor version is released.

# Build Resources

To build the images run these commands from this folder or for the parent project pom:

Install maven generate jars:

``` shell 
mvn clean install
```

In order to build the docker images, you need to specify *docker.namespace* and *docker.password* properties (here without running tests):

``` shell
MAVEN_OPS='-Xmx2048 -Xms1024' mvn clean install -DskipTests -Ddocker.namespace=eclipsebasyx -Ddocker.password=""
```

You can now check your images from command-line and push the images:
``` shell 
docker images   ...
```
Or you can directly push them from maven. 

``` shell 
MAVEN_OPS='-Xmx2048 -Xms1024' mvn deploy -Ddocker.registry=docker.io -Ddocker.namespace=eclipsebasyx -Ddocker.password=pwd
```
In addition, maven deploy will also deploy your maven artifacts, so you can do everything in one step.

Have a look at the *docker-compose* sub-folder to see how the created images could be referenced in docker-compose files.

Consider updating the [image name pattern](pom.xml#L16) if you want a different image name.

## Configure Favicon
To configure the favicon, add the favicon.ico to [basyx-java-server-sdk\basyx.common\basyx.http\src\main\resources\static](../basyx.common/basyx.http/src/main/resources/static/).
