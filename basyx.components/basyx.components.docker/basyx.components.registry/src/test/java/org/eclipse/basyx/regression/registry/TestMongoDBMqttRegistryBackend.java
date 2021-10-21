package org.eclipse.basyx.regression.registry;

import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.junit.Test;

public class TestMongoDBMqttRegistryBackend extends TestMqttRegistryBackend {
	@Test
	public void testEventsWithMongoDBBackend() {
		RegistryComponent mongoDBRegistryComponent = createMongoDBRegistryComponent();
		testMqttEventForRegistryComponent(mongoDBRegistryComponent);
	}

	private RegistryComponent createMongoDBRegistryComponent() {
		BaSyxMongoDBConfiguration mongoDBConfig = new BaSyxMongoDBConfiguration();
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		RegistryComponent registryComponent = new RegistryComponent(contextConfig, mongoDBConfig);
		return registryComponent;
	}
}
