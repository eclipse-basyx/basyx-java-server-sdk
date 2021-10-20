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

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;



/**
 * Test SQL map implementation with set operations
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestMapSetOperations {

	
	/**
	 * Store SQL root element reference
	 * - An SQL root element is the main gateway to a SQL database
	 */
	protected SQLRootElement sqlRootElement = null;

	
	
	/**
	 * Test basic operations
	 */
	@Test @SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		// Add multiple elements
		Map<String, Object> addedElements1 = new HashMap<>();
		// - Add map elements
		addedElements1.put("El1", 1);
		addedElements1.put("El2", 2);
		addedElements1.put("El3", 3);
		// - Add elements to map
		sqlMap.putAll(addedElements1);
		
		// Test if map is empty
		assertTrue(!sqlMap.isEmpty());
		
		// Test map size and contained elements
		assertTrue(sqlMap.size() == 3);
		assertTrue((int) sqlMap.get("El1") == 1);
		assertTrue((int) sqlMap.get("El2") == 2);
		assertTrue((int) sqlMap.get("El3") == 3);

		// Add multiple elements again
		Map<String, Object> addedElements2 = new HashMap<>();
		// - Add map elements
		addedElements2.put("El1", 'x');
		addedElements2.put("El4", 4);
		addedElements2.put("El5", 5);
		// - Add elements to map
		sqlMap.putAll(addedElements2);

		// Test map size and contained elements
		assertTrue(sqlMap.size() == 5);
		assertTrue((char) sqlMap.get("El1") == 'x');
		assertTrue((int) sqlMap.get("El2") == 2);
		assertTrue((int) sqlMap.get("El3") == 3);
		assertTrue((int) sqlMap.get("El4") == 4);
		assertTrue((int) sqlMap.get("El5") == 5);
		
		// Change value
		sqlMap.put("El5", "xy");
		assertTrue(sqlMap.size() == 5);
		assertTrue(sqlMap.get("El5").equals("xy"));
		
		// Check key set
		// - Expected set
		Set<String> keySetBaseLine = new HashSet<>();
		keySetBaseLine.add("El1");
		keySetBaseLine.add("El2");  
		keySetBaseLine.add("El3");  
		keySetBaseLine.add("El4");  
		keySetBaseLine.add("El5");  
		assertTrue(((Set) sqlMap.keySet()).containsAll(keySetBaseLine));
		assertTrue(keySetBaseLine.containsAll((Set) sqlMap.keySet()));

		// Check values
		Collection valuesBaseLine1 = new LinkedList<>();
		valuesBaseLine1.add('x');
		valuesBaseLine1.add(2);
		valuesBaseLine1.add(3);
		valuesBaseLine1.add(4);
		valuesBaseLine1.add("xy");
		assertTrue(((Collection) sqlMap.values()).containsAll(valuesBaseLine1));
		assertTrue(valuesBaseLine1.containsAll((Collection) sqlMap.values()));
		
		// Change a value again, so that two equal values are in map
		sqlMap.put("El3", 2);
		
		// Check values again		
		Collection valuesBaseLine2 = new LinkedList<>();
		valuesBaseLine2.add('x');
		valuesBaseLine2.add(2);
		valuesBaseLine2.add(2);
		valuesBaseLine2.add(4);
		valuesBaseLine2.add("xy");
		assertTrue(((Collection) sqlMap.values()).containsAll(valuesBaseLine2));
		assertTrue(valuesBaseLine2.containsAll((Collection) sqlMap.values()));
		
		// Get entry set
		// - Base line entry set
		Set<Entry<String, Object>> entrySetBaseLine1 = new HashSet<>();
		entrySetBaseLine1.add(new AbstractMap.SimpleEntry<String, Object>("El1", 'x'));
		entrySetBaseLine1.add(new AbstractMap.SimpleEntry<String, Object>("El2", 2));
		entrySetBaseLine1.add(new AbstractMap.SimpleEntry<String, Object>("El3", 2));
		entrySetBaseLine1.add(new AbstractMap.SimpleEntry<String, Object>("El4", 4));
		entrySetBaseLine1.add(new AbstractMap.SimpleEntry<String, Object>("El5", "xy"));
		assertTrue(((Collection) sqlMap.entrySet()).containsAll(entrySetBaseLine1));
		assertTrue(entrySetBaseLine1.containsAll((Collection) sqlMap.entrySet()));
		
		// Clear map, check size
		sqlMap.clear();
		assertTrue(sqlMap.size() == 0);
		
		// Test if map is empty
		assertTrue(sqlMap.isEmpty());

		// Drop tables
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
