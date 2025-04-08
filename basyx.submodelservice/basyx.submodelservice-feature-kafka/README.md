# Submodel Repository - KAFKA Eventing

This feature provides KAFKA eventing. Messages are sent whenever a resource in the submodel repository is created, modified, or deleted.

It is essential to maintain the insertion order of events per submodel. Events for creating, updating, and deleting must not overtake one another. For this reason, only one topic is used, and all messages related to a submodel are stored in a single partition, as the submodel ID is used as the message key. Only within a partition is the order guaranteed when consuming messages.

## Feature Configuration

The feature is configured using the following Spring properties:

| Property                                           |  Default         | Description                                                                                |
|----------------------------------------------------|------------------|--------------------------------------------------------------------------------------------|
| basyx.submodelservice.feature.kafka.enabled        |     false        | Specifies whether the feature is enabled                                                   |
| basyx.feature.kafka.enabled                        |     false        | Specifies whether the feature is enabled (for both aas-repository and submodel-repository) |
| basyx.submodelservice.feature.kafka.topic.name     |  submodel-events | The name of the topic where events are sent                                                |
| basyx.submodelservice.feature.kafka.submodelevents | false            | Specifies whether to send submodel creation and deletion events when starting and tearing down the submodel service |
| spring.kafka.bootstrap-servers                     |     -            | The address of the Kafka brokers, e.g., `PLAINTEXT_HOST://localhost:9092`                                     |


## Message Structure

The messages are transmitted as strings in JSON format:

```json
{
    "type": "SM_CREATED",
    "id": "http://sm.ids.org/1",
    "submodel": {
        "modelType": "Submodel",
        "id": "http://sm.ids.org/1",
        "idShort": "1"
    },
    "smElement": null,
    "smElementPath": null
}
```

Depending on the event type, the fields of the JSON message are populated. The following event types are available:

* SM_CREATED
* SM_UPDATED
* SM_DELETED
* SME_UPDATED
* SME_CREATED
* SME_DELETED