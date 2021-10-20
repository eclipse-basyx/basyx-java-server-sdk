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

/**
 * Externalizes the access data to the SQL database for the SQL test cases
 * 
 * @author schnicke
 *
 */
public class SQLConfig {
	// User for the database
	public static final String SQLUSER = "postgres";

	// Password for the database
	public static final String SQLPW = "admin";
}
