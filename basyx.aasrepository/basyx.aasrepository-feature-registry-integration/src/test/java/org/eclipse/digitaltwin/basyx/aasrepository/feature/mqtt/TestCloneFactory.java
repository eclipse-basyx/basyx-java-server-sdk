package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.CloneFactory;
import org.junit.Test;

public class TestCloneFactory {
	
	@Test
	public void createClone() {
		List<LangStringTextType> expectedDescriptions = RegistryIntegrationTestHelper.getAasRegLangStringTextTypes();
		
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType, LangStringTextType> cloneFactory = new CloneFactory<>(LangStringTextType.class);
		
		List<LangStringTextType> actualDescriptions = cloneFactory.create(RegistryIntegrationTestHelper.getAas4jLangStringTextTypes());
		
		assertEquals(expectedDescriptions.size(), actualDescriptions.size());
		assertEquals(expectedDescriptions, actualDescriptions);
	}

}
