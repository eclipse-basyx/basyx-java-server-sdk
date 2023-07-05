# Submodel Registry Server Kafka Events

This registry storage implementation uses [Apache Kafka](https://kafka.apache.org/) as event sink. Include this dependency if you want to use this storage implementation:

```xml

	<dependency>
		<groupId>org.eclipse.digitaltwin.basyx</groupId>
		<artifactId>basyx.submodelregistry-kafka-events</artifactId>
	</dependency>
```

Then included, you can activate it by either setting the active profile or the "events.sink" property:
```
 -Dspring.profiles.active=kafkaEvents,inMemory
``

Dont't forget to also set the kafka bootstrap servers as property:

```
-Dspring.kafka.bootstrap-servers=PLAINTEXT://kafka:29092
```
Or set the environment variable:
```
KAFKA_BOOTSTRAP_SERVERS=PLAINTEXT://kafka:29092
```



