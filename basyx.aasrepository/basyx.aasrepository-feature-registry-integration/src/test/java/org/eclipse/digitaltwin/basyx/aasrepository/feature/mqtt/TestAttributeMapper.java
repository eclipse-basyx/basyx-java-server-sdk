package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.AttributeMapper;
import org.junit.Test;

public class TestAttributeMapper {
	
	@Test
	public void mapDescriptions() {
		List<LangStringTextType> expectedDescriptions = RegistryIntegrationTestHelper.getAasRegLangStringTextTypes();
		
		List<LangStringTextType> actualDescriptions = new AttributeMapper().mapDescription(RegistryIntegrationTestHelper.getAas4jLangStringTextTypes());
		
		assertEquals(expectedDescriptions.size(), actualDescriptions.size());
		assertEquals(expectedDescriptions, actualDescriptions);
	}
	
	@Test
	public void mapAdministration() {
		AdministrativeInformation expectedAdministrativeInformation = RegistryIntegrationTestHelper.getAasRegAdministration();
		
		AdministrativeInformation actualAdministrativeInformation = new AttributeMapper().mapAdministration(RegistryIntegrationTestHelper.getAas4jAdministration());
		
		assertEquals(expectedAdministrativeInformation, actualAdministrativeInformation);
	}

}
