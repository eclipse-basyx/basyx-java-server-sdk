package org.eclipse.digitaltwin.basyx.aasrepository.feature.mqtt;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.AttributeMapper;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.CloneFactory;
import org.junit.Test;

public class TestCloneFactory {
	
	@Test
	public void createCloneOfListType() {
		List<LangStringTextType> expectedDescriptions = RegistryIntegrationTestHelper.getAasRegLangStringTextTypes();
		
		CloneFactory<org.eclipse.digitaltwin.aas4j.v3.model.LangStringTextType, LangStringTextType> cloneFactory = new CloneFactory<>(LangStringTextType.class);
		
		List<LangStringTextType> actualDescriptions = cloneFactory.create(RegistryIntegrationTestHelper.getAas4jLangStringTextTypes());
		
		assertEquals(expectedDescriptions.size(), actualDescriptions.size());
		assertEquals(expectedDescriptions, actualDescriptions);
	}
	
//	@Test
//	public void createCloneOfNonListType() {
//		AssetKind expectedAssetKind = RegistryIntegrationTestHelper.AASREG_ASSET_KIND;
//		
//		AssetKind actualAssetKind = new AttributeMapper().mapAssetKind(RegistryIntegrationTestHelper.AAS4J_ASSET_KIND);
//
//		assertEquals(expectedAssetKind, actualAssetKind);
//	}

}
