# BaSyx Databridge Example Setup

This example showcases the usage of the BaSyx Databridge. The BaSyx Databridge is a service that allows the exchange of data between assets and the AAS.
Here, an example MQTT client for an environmental sensor is used to send data via the BaSyx Databridge to an AAS located in the BaSyx AAS Environment.

## How to run the BaSyx Databridge Example

1. Open a terminal in this folder
2. Run the following command to start the BaSyx containers:

```bash
docker-compose up -d
```

> To run the example containers, you need to have Docker installed on your device.

## View the working Example

To see the working example, open the [BaSyx AAS Web UI](http://localhost:3000) and navigate to the `SensorExampleAAS`. You can see the data coming from the MQTT client in the `SensorData` submodel.
To see updates in real-time, active the `Auto-Sync` feature in the AAS Web UI (see top right corner of the UI).

## Where to find the configuration

### BaSyx Databridge

The configuration for the BaSyx Databridge can be found in the `databridge` folder. There you can find the `routes.config` file, which defines the routes from the assets data source to the AAS data sink.
The data source configuration can be found in the `mqttconsumer.json` file, and the data sink configuration can be found in the `aasserver.json` file.
There are also data transformers defined for extracting the data from the MQTT message.
For more details on how to configure the BaSyx Databridge, please refer to the [BaSyx Databridge documentation](https://wiki.basyx.org/en/latest/content/user_documentation/basyx_components/databridge/index.html).

### MQTT Client

The configuration for the MQTT client can be found in the `mqtt-publisher` folder. It is a small Python script that publishes data to the MQTT broker.

### MQTT Broker

The MQTT brokers configuration can be found in the `mosquitto` folder. The configuration is defined in the `config/mosquitto.conf` file.

### BaSyx Components

The configuration for the BaSyx components can be found in the `basyx` folder.
The AAS used in this example is located in the `aas` folder.
