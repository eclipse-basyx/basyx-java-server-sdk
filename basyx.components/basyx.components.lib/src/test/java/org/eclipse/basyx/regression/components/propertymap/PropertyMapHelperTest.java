/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/


package org.eclipse.basyx.regression.components.propertymap;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eclipse.basyx.components.propertymap.PropertyMapHelper;
import org.junit.Test;

/**
 * 
 * @author schnicke
 *
 */
public class PropertyMapHelperTest {

	@Test
	public void testGetListFromStringListMultipleElementsWithSpace() {
		String testData = "a, b, c";
		List<String> expected = Arrays.asList("a", "b", "c");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);
		
		assertEquals(expected, actual);
	}

	@Test
	public void testGetListFromStringListMultipleElementsNoSpace() {
		String testData = "a,b, c";
		List<String> expected = Arrays.asList("a", "b", "c");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);

		assertEquals(expected, actual);
	}

	@Test
	public void testGetListFromStringListSingleElementWithSpace() {
		String testData = "a ";
		List<String> expected = Arrays.asList("a");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);

		assertEquals(expected, actual);
	}

	@Test
	public void testGetListFromStringListSingleElementNoSpace() {
		String testData = "a";
		List<String> expected = Arrays.asList("a");
		List<String> actual = PropertyMapHelper.getListFromStringList(testData);

		assertEquals(expected, actual);
	}
}
