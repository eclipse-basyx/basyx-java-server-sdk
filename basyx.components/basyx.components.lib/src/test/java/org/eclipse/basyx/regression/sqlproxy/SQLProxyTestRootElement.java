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

import org.eclipse.basyx.tools.sqlproxy.SQLRootElement;
import org.junit.Test;



/**
 * Test SQL root element implementation, its creation, and dropping
 * 
 * @author kuhn
 *
 */
public class SQLProxyTestRootElement {

	
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
		// - Create new table in database for root element
		sqlRootElement.createRootTableIfNotExists();
		
		// Get element IDs
		assertTrue(sqlRootElement.getNextIdentifier() == 1);
		assertTrue(sqlRootElement.getNextIdentifier() == 2);
		
		// Create map
		sqlRootElement.createMap(0);
		
		// Create collection
		sqlRootElement.createCollection(1);
		

		// Drop tables
		sqlRootElement.dropTable(0);
		sqlRootElement.dropTable(1);

		// Drop table for root element (= delete it)
		sqlRootElement.drop();
	}
}
