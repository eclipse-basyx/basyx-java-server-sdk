/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.sql.query;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.basyx.components.tools.propertyfile.opdef.Parameter;
import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.eclipse.basyx.tools.sql.driver.SQLDriver;




/**
 * SQL query operation
 * 
 * @author kuhn
 *
 */
public class DynamicSQLRunner {

	
	/**
	 * Store SQL driver instance
	 */
	protected ISQLDriver sqlDriver = null;
	
	
	
	
	
	/**
	 * Constructor that accepts a driver
	 */
	public DynamicSQLRunner(ISQLDriver driver) {
		// Store SQL driver instance
		sqlDriver = driver;
	}

	
	/**
	 * Constructor
	 */
	public DynamicSQLRunner(String path, String user, String pass, String qryPfx, String qDrvCls) {
		// Create SQL driver instance
		sqlDriver = new SQLDriver(path, user, pass, qryPfx, qDrvCls);
	}
	
	
	
	/**
	 * Get method parameter definition
	 */
	protected Class<?>[] getMethodParameter(Collection<Parameter> parameter) {
		// Store operation signature
		Class<?>[] result = new Class<?>[2];
		
		// Operation signature is ResultSet and a list of string parameter that define column names
		result[0] = ResultSet.class;
		result[1] = Object[].class;
		
		// Return signature
		return result;
	}
	
	
	/**
	 * Get column names
	 */
	protected Collection<String> getColumnNames(Collection<Parameter> parameter) {
		// Return value
		Collection<String> result = new LinkedList<>();
		
		for (Parameter par: parameter) result.add(par.getName());
		
		return result;
	}
}

