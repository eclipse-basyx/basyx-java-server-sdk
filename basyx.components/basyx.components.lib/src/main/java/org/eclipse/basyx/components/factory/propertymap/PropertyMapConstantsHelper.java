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

/**
 * Provides utility methods for working with property maps
 * 
 * @author schnicke
 *
 */
public class PropertyMapConstantsHelper {

	/**
	 * Retrieves the IdShort stored in a property map
	 * 
	 * @param map
	 * @return
	 */
	public static String getIdShort(Map<String, String> map) {
		return map.get(PropertyMapConstants.IDSHORT);
	}

	/**
	 * Retrieves the Id value stored in a property map
	 * 
	 * @param map
	 * @return
	 */
	public static String getIdValue(Map<String, String> map) {
		return map.get(PropertyMapConstants.IDVALUE);
	}
}
