# Eclipse BaSyx - AAS Environment
Eclipse BaSyx provides the AAS Environment as off-the-shelf component:

    docker run --name=aas-env -p:8081:8081 -v C:/tmp:/usr/share/config eclipsebasyx/aas-environment:2.0.0-SNAPSHOT 

It aggregates the AAS Repository, Submodel Repository and ConceptDescription Repository into a single component. For its features and configuration, see the documentation of the respective components.
