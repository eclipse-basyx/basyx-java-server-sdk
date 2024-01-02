# Developer Documentation
This file provides in-depth documentation inteded for developers. If your goal is using the off-the-shelf components as-is, please refer to the [Components Documentation](Components.md).
Please note that this documentation is work-in-progress and will be extended continuously.

## Building Docker Images
Each component (i.e., projects ending in the _components_ suffix) contain a DockerFile describing the Docker Image. For building it, Maven is utilized via:

  ```
mvn clean install -Ddocker.namespace=eclipsebasyx
  ```

By defining the _docker.namespace_ variable, the maven docker plugin is enabled and the image is build as well as integration tests are being executed.
