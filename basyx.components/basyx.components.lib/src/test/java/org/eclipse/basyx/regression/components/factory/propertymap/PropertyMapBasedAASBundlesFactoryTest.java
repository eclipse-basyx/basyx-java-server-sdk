/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package org.eclipse.basyx.regression.components.factory.propertymap;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapBasedAASBundlesFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAASBundlesFactoryTest {
	private static final Property PROP1 = new Property("prop1", "Hello");
	private static final Property PROP2 = new Property("prop2", "123");
	private static final Submodel SM1 = getSM1();
	private static final Asset ASSET = new Asset("asset1IdShort", new CustomId("asset1"), AssetKind.INSTANCE);
	private static final AssetAdministrationShell AAS1 = getAAS1();
	private static final AssetAdministrationShell AAS2 = getAAS2();

	@Test
	public void testAASBundleCreation() {
		Map<String, Object> properties = buildPropertyMap();
		Set<AASBundle> expected = getExpectedBundles();
		Set<AASBundle> actual = new PropertyMapBasedAASBundlesFactory().create(properties);

		assertEquals(expected, actual);
	}

	private Map<String, Object> buildPropertyMap() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(PropertyMapBasedAASBundlesFactory.SHELLS, getAASIdStringList());
		properties.put(PropertyMapBasedAASBundlesFactory.ASSETS, getAssetIdStringList());
		properties.put(PropertyMapBasedAASBundlesFactory.SUBMODELS, getSubmodelIdStringList());
		properties.put(PropertyMapBasedAASBundlesFactory.PROPERTIES, getPropertyIdStringList());

		properties.putAll(buildAsset1Map());
		properties.putAll(buildAAS1Map());
		properties.putAll(buildAAS2Map());
		properties.putAll(buildSM1Map());
		properties.putAll(buildPropertiesMap());

		return properties;
	}

	private Map<String, Object> buildAsset1Map() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ASSET.getIdentification().getId(), PropertyMapBasedAssetFactoryTest.buildAssetMap(ASSET.getIdentification().getId()));
		return properties;
	}

	private String getPropertyIdStringList() {
		return PROP1.getIdShort() + ", " + PROP2.getIdShort();
	}

	private String getSubmodelIdStringList() {
		return SM1.getIdentification().getId();
	}

	private String getAssetIdStringList() {
		return ASSET.getIdentification().getId();
	}

	private Map<String, Object> buildPropertiesMap() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(PROP1.getIdShort(), PropertyMapBasedPropertyFactoryTest.buildPropertyMap(PROP1.getIdShort(), (String) PROP1.getValue()));
		properties.put(PROP2.getIdShort(), PropertyMapBasedPropertyFactoryTest.buildPropertyMap(PROP2.getIdShort(), (String) PROP2.getValue()));

		return properties;
	}

	private Map<String, Object> buildSM1Map() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(SM1.getIdentification().getId(), PropertyMapBasedSubmodelFactoryTest.buildSubmodelMap(SM1.getIdShort(), SM1.getIdentification().getId(), getSM1PropertyString()));

		return properties;
	}

	private String getSM1PropertyString() {
		return PROP1.getIdShort() + ", " + PROP2.getIdShort();
	}

	private Map<String, Object> buildAAS1Map() {
		Map<String, Object> properties = new HashMap<>();
		String aas1Id = AAS1.getIdentification().getId();
		properties.put(aas1Id, buildAASMapContent(AAS1, ASSET, SM1));

		return properties;
	}

	private Map<String, Object> buildAAS2Map() {
		Map<String, Object> properties = new HashMap<>();
		String aas1Id = AAS2.getIdentification().getId();
		properties.put(aas1Id, buildAASMapContent(AAS2, ASSET, SM1));

		return properties;
	}

	private Map<String, String> buildAASMapContent(AssetAdministrationShell shell, Asset asset, Submodel submodel) {
		String aas1Id = shell.getIdentification().getId();
		String asset1Id = asset.getIdentification().getId();
		String sm1Id = submodel.getIdentification().getId();

		return PropertyMapBasedAASFactoryTest.buildAASMap(aas1Id, asset1Id, sm1Id);
	}

	private static String getAASIdStringList() {
		return AAS1.getIdentification().getId() + ", " + AAS2.getIdentification().getId();
	}

	private Set<AASBundle> getExpectedBundles() {
		return new HashSet<>(Arrays.asList(buildAASBundle1(), buildAASBundle2()));
	}

	private AASBundle buildAASBundle1() {
		return new AASBundle(getAAS1(), Collections.singleton(SM1));
	}

	private AASBundle buildAASBundle2() {
		return new AASBundle(getAAS2(), Collections.singleton(SM1));
	}

	private static AssetAdministrationShell getAAS1() {
		AssetAdministrationShell shell = new AssetAdministrationShell("aas1IdShort", new CustomId("aas1"), ASSET);
		shell.addSubmodelReference(SM1.getReference());

		return shell;
	}

	private static AssetAdministrationShell getAAS2() {
		AssetAdministrationShell shell = new AssetAdministrationShell("aas2IdShort", new CustomId("aas2"), ASSET);
		shell.addSubmodelReference(SM1.getReference());

		return shell;
	}

	private static Submodel getSM1() {
		Submodel submodel = new Submodel("sm1IdShort", new CustomId("sm1"));
		submodel.addSubmodelElement(PROP1);
		submodel.addSubmodelElement(PROP2);

		return submodel;
	}
}
