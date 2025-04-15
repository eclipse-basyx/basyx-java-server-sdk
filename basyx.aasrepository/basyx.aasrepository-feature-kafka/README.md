# AssetAdministrationShell Repository - KAFKA Eventing

This feature provides KAFKA eventing. Messages are sent whenever a resource in the repository is created, updated, or deleted.

A key concern is that the insertion order is preserved for each Shell event. Events for creating, updating, and deleting must not overtake each other. For this reason, only one topic is used, and all messages related to a specific Shell are placed in one partition, by using the Shell ID as the message key. Only within a partition is the order of messages guaranteed to be maintained when consumed.

## Configuration of the Feature

The feature is configured through the following Spring properties:

| Property                                      | Default         | Description                                                                                  |
|-----------------------------------------------|-----------------|----------------------------------------------------------------------------------------------|
| basyx.aasrepository.feature.kafka.enabled     | false           | Specifies whether the feature is enabled for AAS repository                                  |
| basyx.feature.kafka.enabled                   | false           | Specifies whether the feature is enabled for both the AAS repository and Submodel repository |
| basyx.aasrepository.feature.kafka.topic.name  | aas-events      | The name of the topic where events are sent                                                  |
| spring.kafka.bootstrap-servers                | -               | The address of the Kafka brokers, e.g., PLAINTEXT_HOST://localhost:9092                      |

## Structure of the Messages

The values are transferred as strings in JSON format:

```json
{
	"type": "AAS_CREATED",
	"id" : "http://aas.ids.org/1",
	"aas" : {
		"modelType": "AssetAdministrationShell",
		"id" : "http://aas.ids.org/1",
		"idShort" : "1"
	},
	"submodelId" : null,
	"reference" : null,
	"assetInformation" : null
}
```

Depending on the event type, the fields of the JSON message are set. The following event types are available:

* AAS_CREATED
* AAS_UPDATED
* AAS_DELETED
* SM_REF_ADDED
* SM_REF_DELETED
* ASSET_INFORMATION_SET


