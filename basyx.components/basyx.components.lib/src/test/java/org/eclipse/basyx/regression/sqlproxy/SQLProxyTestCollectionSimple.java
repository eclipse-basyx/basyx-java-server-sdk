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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.basyx.tools.sqlproxy.SQLCollection;
import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;



/**
 * Test SQL collection element implementation and its basic operations
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestCollectionSimple {

	
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

		// Create collection
		Collection<Object> sqlColl = sqlRootElement.createCollection(1);

		
		// Test simple collection functions
		assertTrue(sqlColl.size() == 0);
		// - Add single element
		sqlColl.add("A"); assertTrue(sqlColl.size() == 1);
		sqlColl.add("B"); assertTrue(sqlColl.size() == 2);
		sqlColl.add("A"); assertTrue(sqlColl.size() == 3);
		// - Check element presence
		assertTrue(sqlColl.contains("A") == true);
		assertTrue(sqlColl.contains("B") == true);
		assertTrue(sqlColl.contains("C") == false);
		// - Remove element
		sqlColl.remove("A"); 
		assertTrue(sqlColl.size() == 1); 
		assertTrue(sqlColl.isEmpty() == false);
		// - Clear collection
		sqlColl.clear(); 
		assertTrue(sqlColl.size() == 0); 
		assertTrue(sqlColl.isEmpty() == true);
		
		
		// Test collection iterator
		assertTrue(sqlColl.size() == 0);
		// - Fill collection
		sqlColl.add("A"); assertTrue(sqlColl.size() == 1);
		sqlColl.add("B"); assertTrue(sqlColl.size() == 2);
		sqlColl.add("A"); assertTrue(sqlColl.size() == 3);
		sqlColl.add("C"); assertTrue(sqlColl.size() == 4);
		// - Iterate elements
		Iterator<Object> it1 = sqlColl.iterator();
		assertTrue(it1.hasNext() == true); assertTrue(it1.next().equals("A"));
		assertTrue(it1.hasNext() == true); assertTrue(it1.next().equals("B"));
		assertTrue(it1.hasNext() == true); assertTrue(it1.next().equals("A"));
		assertTrue(it1.hasNext() == true); assertTrue(it1.next().equals("C"));
		assertTrue(it1.hasNext() == false);
		// - Test conversion to generic object array
		Object[] elements1 = sqlColl.toArray();
		assertTrue(elements1.length == 4);
		assertTrue(elements1[0].equals("A"));
		assertTrue(elements1[1].equals("B"));
		assertTrue(elements1[2].equals("A"));
		assertTrue(elements1[3].equals("C"));
		// - Test conversion to typed array
		String[] elements2 = sqlColl.toArray(new String[4]);
		assertTrue(elements2.length == 4);
		assertTrue(elements2[0].equals("A"));
		assertTrue(elements2[1].equals("B"));
		assertTrue(elements2[2].equals("A"));
		assertTrue(elements2[3].equals("C"));
		// - Iterate elements again
		Iterator<Object> it2 = sqlColl.iterator();
		assertTrue(it2.hasNext() == true); assertTrue(it2.next().equals("A"));
		assertTrue(it2.hasNext() == true); assertTrue(it2.next().equals("B"));
		assertTrue(it2.hasNext() == true); assertTrue(it2.next().equals("A"));
		assertTrue(it2.hasNext() == true); assertTrue(it2.next().equals("C"));
		assertTrue(it2.hasNext() == false);
		
		
		// Get all elements from collection
		Object[] arr = sqlColl.toArray();
		// - Check array contents
		assertTrue(arr.length == 4);
		assertTrue(arr[0].equals("A"));
		assertTrue(arr[1].equals("B"));
		assertTrue(arr[2].equals("A"));
		assertTrue(arr[3].equals("C"));

		
		// Connect to collection
		Collection<Object> sqlColl2 = new SQLCollection(sqlRootElement, 1);
		// - Iterate elements again
		Iterator<Object> it3 = sqlColl2.iterator();
		assertTrue(it3.hasNext() == true); assertTrue(it3.next().equals("A"));
		assertTrue(it3.hasNext() == true); assertTrue(it3.next().equals("B"));
		assertTrue(it3.hasNext() == true); assertTrue(it3.next().equals("A"));
		assertTrue(it3.hasNext() == true); assertTrue(it3.next().equals("C"));
		assertTrue(it3.hasNext() == false);

		
		// Drop tables
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
