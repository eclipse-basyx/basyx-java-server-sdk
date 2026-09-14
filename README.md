# Eclipse BaSyx Java V2 SDK [![Docker Pulls](https://img.shields.io/docker/pulls/eclipsebasyx/aas-server?style=plastic)](https://hub.docker.com/search?q=eclipsebasyx)

[![BaSyx Logo](https://www.eclipse.org/basyx/img/basyxlogo.png)](https://www.eclipse.org/basyx/)

BaSyx Java V2 is a server and client SDK for building Java applications compatible with *Details of the Asset Administration Shell V3*. It provides reusable components, client libraries, integrations such as MQTT, and backends including InMemory and MongoDB.

For up-to-date, ready-to-run server components, use [BaSyx Go](https://go.basyx.org/) on [Docker Hub](https://hub.docker.com/search?q=eclipsebasyx). See [why BaSyx Go?](https://go.basyx.org/#why) for a comparison with BaSyx Java.

## Get started with our off-the-shelf components

> [!IMPORTANT]
> We provide a **[Starter Kit](https://basyx.org/get-started/introduction)** on [BaSyx.org](https://basyx.org) that can be used to create your first running BaSyx setup tailored to your needs.  
> All configurations can be done graphically with ease.

> [!NOTE]
> If you need a minimal example, we also provide one [here](examples/BaSyxMinimal) in our collection of [BaSyx examples](examples).

## Server SDK

The following off-the-shelf components are available:

* [AAS Repository](basyx.aasrepository)
* [Submodel Repository](basyx.submodelrepository)
* [ConceptDescription Repository](basyx.conceptdescriptionrepository)
* [AAS Environment](basyx.aasenvironment)
* [AAS Registry](basyx.aasregistry)
* [Submodel Registry](basyx.submodelregistry)
* [AAS Discovery](basyx.aasdiscoveryservice)
* [AASX File Server](basyx.aasxfileserver)

In addition, a blueprint for a Type 2/ Type 3 standalone submodel is provided:

* [Type 2/Type 3 Submodel](basyx.submodelservice)

## Client SDK

In addition, the following Clients are available:

* [AAS Repository Client](basyx.aasrepository/basyx.aasrepository-client)
* [Submodel Repository Client](basyx.submodelrepository/basyx.submodelrepository-client)
* [AAS Service Client](basyx.aasservice/basyx.aasservice-client)
* [Submodel Service Client](basyx.submodelservice/basyx.submodelservice-client)
* [AAS Environment Client](basyx.aasenvironment/basyx.aasenvironment-client)
* [AAS Registry Client](basyx.aasregistry/basyx.aasregistry-client-native)
* [Submodel Registry Client](basyx.submodelregistry/basyx.submodelregistry-client-native)

## Documentation, Roadmap & Examples

In addition to the [general documentation](https://github.com/eclipse-basyx/basyx-java-server-sdk/tree/main/docs), each component has its own specific documentation that can be found in the respective folders. Additionally, we provide a general documentation on [readthedocs](https://wiki.basyx.org/en/latest/).
Furthermore, we are providing easy to use [examples](examples) that can be leveraged for setting up your own AAS infrastructure.
The future roadmap of BaSyx is described [here](https://github.com/eclipse-basyx/basyx-java-server-sdk/blob/main/docs/Roadmap.md).

## Snapshot Releases

We're distributing our SNAPSHOT releases via DockerHub and Maven Central. For using the snapshots from Maven Central, add the following part to your project's POM:

```xml
<repositories>
  <repository>
    <name>Central Portal Snapshots</name>
    <id>central-portal-snapshots</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

## Getting Involved & Contributing

If you would like to get involved with the BaSyx Community, the [BaSyx Open Hour](https://www.iese.fraunhofer.de/en/customers_industries/digitalisierung-produktion/industrie40/basyx_open_hour.html) (every first Friday of the month) is an excellent starting point.

We encourage you to contribute to BaSyx! Please check out the [Contributing to BaSyx guide](./.github/CONTRIBUTING.md) for guidelines about how to proceed.

Trying to report a possible security vulnerability in BaSyx? Please check out our [security policy](https://github.com/eclipse-basyx/basyx-java-server-sdk/security/policy) for guidelines about how to proceed.

Everyone interacting in BaSyx and its sub-projects' codebases, issue trackers, chat rooms, and mailing lists is expected to follow the BaSyx [code of conduct](https://github.com/eclipse-basyx/basyx-java-server-sdk?tab=coc-ov-file#readme).

## License

BaSyx is released under the [MIT License](https://opensource.org/licenses/MIT).
