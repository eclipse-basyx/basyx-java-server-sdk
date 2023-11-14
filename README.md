# Eclipse BaSyx Java V2 Server SDK [![Docker Pulls](https://img.shields.io/docker/pulls/eclipsebasyx/aas-server?style=plastic)](https://hub.docker.com/search?q=eclipsebasyx)
[![BaSyx Logo](https://www.eclipse.org/basyx/img/basyxlogo.png)](https://www.eclipse.org/basyx/)
 
In this repository, the BaSyx Java V2 components fully compatible with *Details of the Asset Administration Shell V3* are contained. For each component, a multitude of backends (e.g., InMemory, MongoDB) as well as further features (MQTT, ...) are provided. All components are available on [DockerHub](https://hub.docker.com/search?q=eclipsebasyx) as off-the-shelf components and can be easily configured and extended. Additionally, the server SDK of this repository can be used for implementation of further components.

The following off-the-shelf components are available:

* [AAS Repository](basyx.aasrepository)
* [Submodel Repository](basyx.submodelrepository)
* [ConceptDescription Repository](basyx.conceptdescriptionrepository)
* [AAS Environment](basyx.aasenvironment)
* [AAS Registry](basyx.aasregistry)
* [Submodel Registry](basyx.submodelregistry)
* [AAS Discovery](basyx.aasdiscoveryservice)

## Documentation & Examples
In addition to the [general documentation](https://github.com/eclipse-basyx/basyx-java-server-sdk/tree/main/docs), each component has its own specific documentation that can be found in the respective folders. 
Furthermore, we are providing easy to use [examples](examples) that can be leveraged for setting up your own AAS infrastructure.

## Contributing

If you would like to contribute, please notice the [contribution guidelines](CONTRIBUTING.md). The overall process is described in the [Eclipse wiki](https://wiki.eclipse.org/BaSyx_/_Developer_/_Contributing).
