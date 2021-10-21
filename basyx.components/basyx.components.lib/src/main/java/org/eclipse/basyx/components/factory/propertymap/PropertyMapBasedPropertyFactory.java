/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.components.factory.propertymap;

import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

/**
 * Factory enabling {@link Property} creation from a property map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedPropertyFactory {
	public static final String VALUE = "value";

	/**
	 * Creates a {@link Property} based on the data contained in the property map.
	 * Right now, only String is supported as value <br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * { 
	 * 		"idShort": "propertyId", 
	 * 		"value": "TestValue"
	 * }
	 * </pre>
	 * 
	 * @param propertyMap
	 * @return
	 */
	public Property create(Map<String, String> propertyMap) {
		String idShort = PropertyMapConstantsHelper.getIdShort(propertyMap);
		Object value = getValue(propertyMap);
		return new Property(idShort, value);
	}

	private Object getValue(Map<String, String> propertyMap) {
		return propertyMap.get(VALUE);
	}

}
