/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.components.propertymap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides helper methods for working with property maps
 * 
 * @author schnicke
 *
 */
public class PropertyMapHelper {

	/**
	 * Separates a String containing a comma-separated list of entries into a List
	 * of Strings <br>
	 * <br>
	 * Example: getListFromStringList("a,b, c"): ["a", "b", "c"]
	 * 
	 * @param stringList
	 * @return
	 */
	public static List<String> getListFromStringList(String stringList) {
		return Arrays.asList(stringList.split(",")).stream().map(String::trim).collect(Collectors.toList());
	}
}
