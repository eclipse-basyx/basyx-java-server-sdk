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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.components.factory.propertymap.PropertyMapBasedPropertyFactory;
import org.eclipse.basyx.components.factory.propertymap.PropertyMapConstants;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedPropertyFactoryTest {

	@Test
	public void testPropertyCreation() {
		String propIdShort = "Prop1";
		String propValue = "Test123";

		Property expected = buildProperty(propIdShort, propValue);
		Map<String, String> propertyMap = buildPropertyMap(propIdShort, propValue);
		Property actual = new PropertyMapBasedPropertyFactory().create(propertyMap);

		assertEquals(expected, actual);
	}

	/**
	 * Builds a Map containing a Property's attributes
	 * 
	 * @param idShort
	 * @param value
	 * @return
	 */
	public static Map<String, String> buildPropertyMap(String idShort, String value) {
		Map<String, String> propertyMap = new HashMap<>();
		propertyMap.put(PropertyMapConstants.IDSHORT, idShort);
		propertyMap.put(PropertyMapBasedPropertyFactory.VALUE, value);
		return propertyMap;
	}

	private static Property buildProperty(String idShort, Object value) {
		return new Property(idShort, value);
	}
}
