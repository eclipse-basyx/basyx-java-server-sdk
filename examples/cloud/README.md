# Deployment of Basyx-v2 in Kubernetes clusters using Helm

To deploy the full example in a cloud-provider agnostic Kubernetes cluster, [Helm](https://helm.sh/) is a useful orchestration tool to minimize complexity.

To support this, Helm charts are provided here to make it easier to deploy a working Basyx environment, registry and GUI, along with auxiliary services that may be necessary.

## Components

The Umbrella chart in this folder consists of the following component charts.

### Basyx

The configuration for all these components are added as part of `values.basyx.yaml`.

- AAS Environment
- AAS Registry
- AAS GUI

### External

The configuration for external supporting charts are generally added as part of `values.external.yaml` or `values.<service-name>.yaml`

- MongoDB (separate due to external issues)
- Kafka (WIP)
- Mosquitto (WIP)
- others (not currently in scope)

## Caveats

- MongoDB has some issues with being deployed as part of a Composite chart. The instructions thus install this chart separately at first, and then install the remaining altogether.
- With a MongoDB configuration and pre-seeded AASX files, restarting the Basyx AAS Environment is currently broken: the AAS Environment finds conflicting AAS IDs in MongoDB, is unable to make a choice regarding overwriting or accepting the existing AAS shells and submodels, and fails.

## Steps

0. Ensure that you have a working Kubernetes cluster with adequate resources, and access to `kubectl` and `helm` as binaries. A useful UI for visualizing the status of a Kubernetes cluster is Lens (check [OpenLens](https://github.com/MuhammedKalkan/OpenLens)). An Nginx ingress is mandatory to expose services as needed.

    Another nice-to-have tool for identifying differences between deployed releases and local configuration/chart changes is the [helm-diff](https://github.com/databus23/helm-diff) tool.

1. Create a namespace. We use the example namespace `basyx`.

    ```bash
    kubectl create namespace basyx
    ```

2. MongoDB must be deployed separately, due to a conflict/bug in the chart when it is included as part of an Umbrella chart. (release name is `mongodb`). As part of the first step, also add the repo to Helm. Both commands are mentioned below. Change version as necessary.

    ```bash
    helm repo add bitnami https://charts.bitnami.com/bitnami

    helm install -n basyx mongodb bitnami/mongodb --version 14.4.9 -f values.mongodb.yaml
    ```

    Currently both the AAS Environment and AAS Registry need admin/root user rights.
    All other parameters may be set based on the charts.
    TODO: Investigate enabling non-admin users.

    It is not mandatory that the MongoDB service be publicly exposed.

3. (WIP) Add as many external services as needed in the Chart.yaml. Configuration for these must be integrated into the Basyx dependency charts, as parameters or configuration files. Examples for the Kafka and MQTT chart values are commented out in the Chart.yaml file.

    Configurations for all these external services are described in `values.external.yaml`

    With just MongoDB, the core Basyx services will still run.

4. At this point, it is ideal that a LetsEncrypt ClusterIssuer is available on the cluster to provide TLS certificates to the services. Additionally, a domain name is mandatory. For this demo, we work with `example.com` and use subdomains to describe all values. Update this with a proper domain-name for your use-case.

    Also, all ingress templates currently include default annotations for TLS, which may be converted into a configurable form in a later update.

    Thus, the endpoints we will deploy shall be available at:
    - AAS-GUI: `https://aasdashboard.example.com`
    - AAS-Environment: `https://basyx.example.com`
    - AAS-Registry: `https://basyxregistry.example.com`

5. In `values.basyx.yaml`, we must define the configuration for all the Basyx services. While most of the values are self-explanatory and drawn directly from the docker-compose example in the sibling folder, some considerations here include:

    1. AAS-Registry:
        - Choose an appropriate image for the AAS-Registry based on whether a MongoDB   and a Kafka broker are deployed alongside the cluster. (default is `eclipsebasyx/aas-registry-log-mongodb:2.0.0-SNAPSHOT`)
        - In the example values file, the ingress is enabled and is `basyxregistry.example.com`.
        - Configuration for CORS is not perfect, and temporarily may be set to allow all traffic. Upcoming [RBAC support](https://github.com/eclipse-basyx/basyx-java-server-sdk/pull/159) may help solve access-control issues.
    2. AAS-Environment:
        - `startup.enabled = true` is a snippet that does a particularly specific seeding of initial AAS/AASX files from a Git repository into a Kubernetes emptyDir, and then mounts them to the AAS Environment. This is temporary, and further updates may expand the range of initial sources for initial AASX files.
        `startup.repo` is then a string with the link to the Git repository containing the files.
        - In the example values file, the ingress is enabled and is `basyx.example.com`.
        - Configuration for CORS also allows all host sources at this time.
    3. AAS-GUI:
        - In the example values file, the ingress is enabled and is `aasdashboard.example.com`.
        - The environment variables for configuring the AAS environment and AAS registry paths currently works with HTTPS hosts only (plain HTTP, as would be possible with calls to pods in the same namespace in the same cluster, is not permitted and throws "not-secure" errors in console). As a result, both AAS environment and AAS registry must currently have an ingress and certificate each.

            Examples for the configuration endpoint variables are provided.

        - External plugins are possible, and currently supported through ConfigMaps (other possibilities may be added later). An example plugin based on the[TimeSeriesVisualizerPlugin](https://github.com/abhishekmaha23/basyx-gui-timeseries-plugin) example may be added as follows (in this case, it is downloaded locally):

        ```bash
        kubectl create configmap -n basyx time-series-ui-plugin --from-file=config_files/basyx-web-ui/TimeSeriesVisualizerPlugin.vue
        ```

        Now this may be referenced in the `values.basyx.yaml` as follows:

        ```yaml
        externalPlugins:
            enabled: true
            plugins:
            - fileName: TimeSeriesVisualizerPlugin.vue
              configMapName: time-series-ui-plugin
        ```

    4. Now, the full release may be installed. First, add the remote repositories you may need to Helm.

        ```bash
        # For bitnami charts, Kafka, MongoDB, Influx, Grafana and others
        helm repo add bitnami https://charts.bitnami.com/bitnami
        # For Mosquitto
        helm repo add t3n https://storage.googleapis.com/t3n-helm-charts
        ```

        Then, considering that all necessary configurations have been set, comment-out or delete the unused subcharts in `Chart.yaml`.

        Next, do a dependency update to pull the latest version of all charts, including the local ones.

        ```bash
        helm dep update
        ```

        Make sure the current Kubernetes context is the right one. For the first release, the following command may be used to install the release on the Kubernetes cluster (release name is `basyxv2`):

        ```bash
        helm install -n basyx basyxv2 -f values.external.yaml -f values.basyx.yaml . 
        ```

        Subsquent updates may use the following commdands.

        ```bash
        helm upgrade -n basyx basyxv2 -f values.external.yaml -f values.basyx.yaml .
        
        helm diff upgrade -n basyx basyxv2 -f values.external.yaml -f values.basyx.yaml .
        ```

        To uninstall the releases, use the following commands:

        ```bash
        helm uninstall -n basyx mongodb
        
        helm uninstall -n basyx basyxv2
        ```

        Any ConfigMaps or other resources not installed directly as part of this process, must be deleted manually.

## TODOs

1. Add other auxiliary services and integrating them in charts.
2. Configure all features and requirements for AAS Environment.
3. Enable Logo, theming and plugins in a better way in the AAS-GUI.
4. Make the Ingress and TLS configuration a bit more modular.
