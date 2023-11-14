package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
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
	public void mapDisplayNames() {
		List<LangStringNameType> expectedDisplayNames = RegistryIntegrationTestHelper.getAasRegLangStringNameTypes();
		
		List<LangStringNameType> actualDisplayNames = new AttributeMapper().mapDisplayName(RegistryIntegrationTestHelper.getAas4jLangStringNameTypes());
		
		assertEquals(expectedDisplayNames.size(), actualDisplayNames.size());
		assertEquals(expectedDisplayNames, actualDisplayNames);
	}
	
	@Test
	public void mapAssetKind() {
		AssetKind expectedAssetKind = RegistryIntegrationTestHelper.AASREG_ASSET_KIND;
		
		AssetKind actualAssetKind = new AttributeMapper().mapAssetKind(RegistryIntegrationTestHelper.AAS4J_ASSET_KIND);

		assertEquals(expectedAssetKind, actualAssetKind);
	}

}
