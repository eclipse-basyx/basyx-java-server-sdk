/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.sqlproxy;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;



/**
 * Test SQL map implementation with array types
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestMapArrayTypes {

	
	/**
	 * Store SQL root element reference
	 * - An SQL root element is the main gateway to a SQL database
	 */
	protected SQLRootElement sqlRootElement = null;

	
	
	/**
	 * Test basic operations
	 */
	@Test
	public void test() throws Exception {
		// Create SQL root element
		sqlRootElement = new SQLRootElement(SQLConfig.SQLUSER, SQLConfig.SQLPW,  "//localhost/basyx-map?", "org.postgresql.Driver", "jdbc:postgresql:", "root_el_01");

		// Drop tables to make sure we start with a fresh database
		sqlRootElement.dropTable(1);
		sqlRootElement.drop();

		// Create new table in database for root element
		sqlRootElement.createRootTableIfNotExists();

		// Create new SQL map
		Map<String, Object> sqlMap = sqlRootElement.createMap(1);
		
		// Clear map to make sure that no old data from previous test runs is stored in it
		sqlMap.clear();
		
		// Test simple types
		sqlMap.put("intArray",    new int[]    {1, 2, 3});
		sqlMap.put("floatArray",  new float[]  {1.2f, 2.2f, 3.4f});
		sqlMap.put("doubleArray", new double[] {3.2, -2.7});
		sqlMap.put("boolArray",   new boolean[] {true, true, false});
		sqlMap.put("StringArray", new String[] {"x", "y", "z"});
		sqlMap.put("CharArray",   new char[] {'x', 'y', 'z'});

		// Check size
		assertTrue(sqlMap.size() == 6);		

		// Test retrieving of values
		assertTrue(Arrays.equals((int[])     sqlMap.get("intArray"),    new int[]     {1, 2, 3}));
		assertTrue(Arrays.equals((float[])   sqlMap.get("floatArray"),  new float[]   {1.2f, 2.2f, 3.4f}));
		assertTrue(Arrays.equals((double[])  sqlMap.get("doubleArray"), new double[]  {3.2, -2.7}));
		assertTrue(Arrays.equals((boolean[]) sqlMap.get("boolArray"),   new boolean[] {true, true, false}));
		assertTrue(Arrays.equals((String[])  sqlMap.get("StringArray"), new String[]  {"x", "y", "z"}));
		assertTrue(Arrays.equals((char[])    sqlMap.get("CharArray"),   new char[]    {'x', 'y', 'z'}));
		
		// Test value presence
		assertTrue(sqlMap.containsValue(new int[] {1, 2, 3}));

		// Clear map, check size
		sqlMap.clear();
		assertTrue(sqlMap.size() == 0);

		// Drop tables
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
