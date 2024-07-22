# Eclipse BaSyx Java V2 SDK [![Docker Pulls](https://img.shields.io/docker/pulls/eclipsebasyx/aas-server?style=plastic)](https://hub.docker.com/search?q=eclipsebasyx)
[![BaSyx Logo](https://www.eclipse.org/basyx/img/basyxlogo.png)](https://www.eclipse.org/basyx/)
 
In this repository, the BaSyx Java V2 components fully compatible with *Details of the Asset Administration Shell V3* are as well as their respective Clients are contained. For each component, a multitude of backends (e.g., InMemory, MongoDB) as well as further features (MQTT, ...) are provided. All components are available on [DockerHub](https://hub.docker.com/search?q=eclipsebasyx) as off-the-shelf components and can be easily configured and extended. Additionally, the server SDK of this repository can be used for implementation of further components.

## Server SDK
The following off-the-shelf components are available:

* [AAS Repository](basyx.aasrepository)
* [Submodel Repository](basyx.submodelrepository)
* [ConceptDescription Repository](basyx.conceptdescriptionrepository)
* [AAS Environment](basyx.aasenvironment)
* [AAS Registry](basyx.aasregistry)
* [Submodel Registry](basyx.submodelregistry)
* [AAS Discovery](basyx.aasdiscoveryservice)

In addition, a blueprint for a Type 2/ Type 3 standalone submodel is provided:
* [Type 2/Type 3 Submodel](basyx.submodelservice)

## Client SDK
In addition, the following Clients are available:
* [AAS Repository Client](basyx.aasrepository/basyx.aasrepository-client)
* [Submodel Repository Client](basyx.submodelrepository/basyx.submodelrepository-client)
* [AAS Service Client](basyx.aasservice/basyx.aasservice-client)
* [Submodel Service Client](basyx.submodelservice/basyx.submodelservice-client)

## Documentation, Roadmap & Examples
In addition to the [general documentation](https://github.com/eclipse-basyx/basyx-java-server-sdk/tree/main/docs), each component has its own specific documentation that can be found in the respective folders. Additionally, we provide a general documentation on [readthedocs](https://wiki.basyx.org/en/latest/).
Furthermore, we are providing easy to use [examples](examples) that can be leveraged for setting up your own AAS infrastructure.
The future roadmap of BaSyx is described [here](https://github.com/eclipse-basyx/basyx-java-server-sdk/blob/main/docs/Roadmap.md).

## Snapshot Releases
We're distributing our SNAPSHOT releases via DockerHub and Maven Central. For using the snapshots from Maven Central, add the following part to your project's POM:

```
<repository>
	<id>sonatype.snapshots</id>
	<name>Sonatype Snapshot Repository</name>
	<url>https://oss.sonatype.org/content/repositories/snapshots</url>
	<releases>
		<enabled>false</enabled>
	</releases>
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
</repository>
```

## Getting Involved & Contributing
If you would like to get involved with the BaSyx Community, the [BaSyx Open Hour](https://www.iese.fraunhofer.de/en/customers_industries/digitalisierung-produktion/industrie40/basyx_open_hour.html) (every first Friday of the month) is an excellent starting point.

We encourage you to contribute to BaSyx! Please check out the [Contributing to BaSyx guide](./.github/CONTRIBUTING.md) for guidelines about how to proceed.

Trying to report a possible security vulnerability in BaSyx? Please check out our [security policy](https://github.com/eclipse-basyx/basyx-java-server-sdk/security/policy) for guidelines about how to proceed.

Everyone interacting in BaSyx and its sub-projects' codebases, issue trackers, chat rooms, and mailing lists is expected to follow the BaSyx [code of conduct](https://github.com/eclipse-basyx/basyx-java-server-sdk?tab=coc-ov-file#readme).

## License

BaSyx is released under the [MIT License](https://opensource.org/licenses/MIT).
