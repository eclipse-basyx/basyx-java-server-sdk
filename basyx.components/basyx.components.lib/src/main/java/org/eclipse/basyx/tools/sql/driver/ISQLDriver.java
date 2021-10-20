/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.sql.driver;

import java.sql.ResultSet;



/**
 * Database access interface
 * 
 * @author kuhn
 *
 */
public interface ISQLDriver {

	
	/**
	 * Execute a SQL query
	 */
	public ResultSet sqlQuery(String queryString);

	
	/**
	 * Execute a SQL update
	 */
	public void sqlUpdate(String updateString);
}
