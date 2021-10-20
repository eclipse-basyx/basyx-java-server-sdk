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
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;



/**
 * Test SQL collection element implementation, and type support
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestCollectionTypes {

	
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

		
		// Test simple types
		// - Add simple types
		sqlColl.add(null);
		sqlColl.add(14);
		sqlColl.add(-14);
		sqlColl.add(14.2f);
		sqlColl.add(-14.4f);
		sqlColl.add(13.2);
		sqlColl.add(-13.4);
		sqlColl.add(true);
		sqlColl.add(false);
		sqlColl.add("");
		sqlColl.add("abc");
		sqlColl.add('a');
		sqlColl.add('b');
		// - Check size
		assertTrue(sqlColl.size() == 13);
		// - Add array types
		sqlColl.add(new int[]     {1, 2, 3});
		sqlColl.add(new float[]   {1.2f, 2.2f, 3.4f});
		sqlColl.add(new double[]  {3.2, -2.7});
		sqlColl.add(new boolean[] {true, true, false});
		sqlColl.add(new String[]  {"x", "y", "z"});
		sqlColl.add(new char[]    {'x', 'y', 'z'});
		// - Check size
		assertTrue(sqlColl.size() == 19);

		
		// Iterate elements
		Iterator<Object> it = sqlColl.iterator();
		assertTrue(it.hasNext() == true); assertTrue(it.next() == null);
		assertTrue(it.hasNext() == true); assertTrue((int) it.next() ==  14);
		assertTrue(it.hasNext() == true); assertTrue((int) it.next() == -14);
		assertTrue(it.hasNext() == true); assertTrue((float) it.next() ==  14.2f);
		assertTrue(it.hasNext() == true); assertTrue((float) it.next() == -14.4f);
		assertTrue(it.hasNext() == true); assertTrue((double) it.next() ==  13.2);
		assertTrue(it.hasNext() == true); assertTrue((double) it.next() == -13.4);
		assertTrue(it.hasNext() == true); assertTrue((boolean) it.next() == true);
		assertTrue(it.hasNext() == true); assertTrue((boolean) it.next() == false);
		assertTrue(it.hasNext() == true); assertTrue(it.next().equals(""));
		assertTrue(it.hasNext() == true); assertTrue(it.next().equals("abc"));
		assertTrue(it.hasNext() == true); assertTrue((char) it.next() == 'a');
		assertTrue(it.hasNext() == true); assertTrue((char) it.next() == 'b');
		assertTrue(it.hasNext() == true); assertTrue(Arrays.equals((int[])     it.next(), new int[]      {1, 2, 3}));
		assertTrue(it.hasNext() == true); assertTrue(Arrays.equals((float[])   it.next(), new float[]    {1.2f, 2.2f, 3.4f}));
		assertTrue(it.hasNext() == true); assertTrue(Arrays.equals((double[])  it.next(), new double[]   {3.2, -2.7}));
		assertTrue(it.hasNext() == true); assertTrue(Arrays.equals((boolean[]) it.next(), new boolean[]  {true, true, false}));
		assertTrue(it.hasNext() == true); assertTrue(Arrays.equals((String[])  it.next(), new String[]   {"x", "y", "z"}));
		assertTrue(it.hasNext() == true); assertTrue(Arrays.equals((char[])    it.next(), new char[]     {'x', 'y', 'z'}));
		assertTrue(it.hasNext() == false);

		// Drop tables
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
