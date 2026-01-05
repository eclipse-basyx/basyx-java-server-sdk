# BaSyx V2 Apache Example

This folder contains an example configuration for setting up the BaSyx V2 infrastructure with Apache.

After executing 

```bash
docker-compose up -d
```

the following components are started:
* AAS Environment (http://localhost/aas-env/)
* AAS Registry (http://localhost/aas-registry/)
* Submodel Registry (http://localhost/sm-registry/)
* AAS Discovery Service (http://localhost/aas-discovery/)
* AAS Web UI (http://localhost/aas-ui/)

## Configuration

### Apache

The Apache configuration is located in the `apache` folder.
You can change the the path to the BaSyx V2 components by modifying the `apache/httpd.conf` file.

### BaSyx V2 components

The BaSyx V2 Registries are configured in the `docker-compose.yml` file.
You can change the port mappings as well as the context path there.

To change the context path of the AAS Environment and the AAS Discovery Service, you have to modify the `basyx/aas-env.properties` file and the `basyx/aas-discovery.properties` file.

### AAS Env Registry Integration

To ensure that the Registries contain the correct URLs of the AASs and Submodels, the `basyx.externalurl` property in the `basyx/aas-env.properties` file must be set to the URL of the AAS Environment.
