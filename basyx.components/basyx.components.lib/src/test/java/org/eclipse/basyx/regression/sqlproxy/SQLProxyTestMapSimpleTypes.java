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

import java.util.Map;

import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;



/**
 * Test SQL map implementation with simple types
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestMapSimpleTypes {

	
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
		sqlMap.put("intVal1",  14);
		sqlMap.put("intVal2", -14);
		sqlMap.put("floatVal1",  14.2f);
		sqlMap.put("floatVal2", -14.4f);
		sqlMap.put("doubleVal1",  13.2);
		sqlMap.put("doubleVal2", -13.4);
		sqlMap.put("boolVal1",  true);
		sqlMap.put("boolVal2", false);
		sqlMap.put("StringVal1", "");
		sqlMap.put("StringVal2", "abc");
		sqlMap.put("CharVal1", 'a');
		sqlMap.put("CharVal2", 'b');

		// Check size
		assertTrue(sqlMap.size() == 12);		

		// Test retrieving of values
		assertTrue((int) sqlMap.get("intVal1") ==  14);
		assertTrue((int) sqlMap.get("intVal2") == -14);
		assertTrue((float) sqlMap.get("floatVal1") ==  14.2f);
		assertTrue((float) sqlMap.get("floatVal2") == -14.4f);
		assertTrue((double) sqlMap.get("doubleVal1") ==  13.2);
		assertTrue((double) sqlMap.get("doubleVal2") == -13.4);
		assertTrue((boolean) sqlMap.get("boolVal1") == true);
		assertTrue((boolean) sqlMap.get("boolVal2") == false);
		assertTrue(sqlMap.get("StringVal1").equals(""));
		assertTrue(sqlMap.get("StringVal2").equals("abc"));
		assertTrue((char) sqlMap.get("CharVal1") == 'a');
		assertTrue((char) sqlMap.get("CharVal2") == 'b');
		
		// Clear map, check size
		sqlMap.clear();
		assertTrue(sqlMap.size() == 0);

		// Drop tables
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
