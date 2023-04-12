package org.eclipse.digitaltwin.basyx.submodelrepository.feature.mqtt;

import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;

public class MqttSubmodelRepositoryFactory implements SubmodelRepositoryFactory {

	private SubmodelRepositoryFactory decorated;
	private IMqttClient client;
	private MqttSubmodelRepositoryTopicFactory topicFactory;

	public MqttSubmodelRepositoryFactory(SubmodelRepositoryFactory decorated, IMqttClient client, MqttSubmodelRepositoryTopicFactory topicFactory) {
		this.decorated = decorated;
		this.client = client;
		this.topicFactory = topicFactory;
	}

	@Override
	public SubmodelRepository create() {
		return new MqttSubmodelRepository(decorated.create(), client, topicFactory);
	}
}
