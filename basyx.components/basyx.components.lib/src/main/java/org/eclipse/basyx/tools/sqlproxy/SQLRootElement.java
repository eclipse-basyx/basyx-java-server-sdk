/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.sqlproxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.basyx.tools.sql.query.DynamicSQLQuery;
import org.eclipse.basyx.tools.sql.query.DynamicSQLUpdate;



/**
 * Create a root element that connects to SQL database and that contains other maps and collections
 * 
 * @author kuhn
 *
 */
public class SQLRootElement extends SQLConnector {

	
	/**
	 * Constructor
	 * 
	 * @param user        SQL user name
	 * @param pass        SQL password
	 * @param url         SQL server URL
	 * @param driver      SQL driver
	 * @param prefix      JDBC SQL driver prefix
	 * @param tableID     ID of table for this element in database. Every element needs a unique ID
	 */
	public SQLRootElement(String user, String pass, String url, String driver, String prefix, String tableID) {
		// Base constructor
		super(user, pass, url, driver, prefix, tableID);
	}
	
	/**
	 * Creates the root table if it does not exist (including a possibly missing schema)
	 */
	public void createRootTableIfNotExists() {
		createSchema();
		createRootTable();
	}

	/**
	 * Removes the root table if it exists (including its schema if it is empty afterwards)
	 */
	public void drop() {
		dropRootTable();
		dropSchema();
	}

	/**
	 * Get next free identifier for another element
	 */
	public int getNextIdentifier() {
		Map<String, Object> sqlResult = readCurrentElementPointer();

		// Store element ID
		int elementId = (int) sqlResult.get("NextElementID");

		// SQL update statement
		String updateString = "UPDATE elements." + getSqlTableID() + " SET NextElementID=" + (elementId + 1)
				+ ", ElementPrefix='" + sqlResult.get("ElementPrefix") + "'";
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(getDriver(), updateString);

		// Empty parameter set
		Map<String, Object> parameter = new HashMap<>();

		// Execute SQL statement
		dynUpdate.accept(parameter);

		// Return element ID
		return elementId;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> readCurrentElementPointer() {
		// SQL query string
		String queryString = "SELECT * FROM elements." + getSqlTableID();
		DynamicSQLQuery dynQuery = new DynamicSQLQuery(getDriver(), queryString,
				"mapArray(NextElementID:Integer,ElementPrefix:String)");
		
		// Empty parameter set
		Map<String, Object> parameter = new HashMap<>();
		// - Execute query, return the column as
		return (Map<String, Object>) dynQuery.get(parameter);
	}
	

	/**
	 * Creates a schema for the root element
	 */
	protected void createSchema() {
		// SQL command
		String sqlCommandString = "CREATE SCHEMA IF NOT EXISTS elements;";
		DynamicSQLUpdate dynCmd = new DynamicSQLUpdate(getDriver(), sqlCommandString);

		// Parameter for SQL command statement
		Map<String, Object> parameter = new HashMap<>();

		// Execute SQL statement
		dynCmd.accept(parameter);
	}
	
	/**
	 * Removes the schema of the root element if it is empty
	 */
	protected void dropSchema() {
		// SQL command
		String sqlCommandString = "DROP SCHEMA IF EXISTS elements RESTRICT;";
		DynamicSQLUpdate dynCmd = new DynamicSQLUpdate(getDriver(), sqlCommandString);

		// Parameter for SQL command statement
		Map<String, Object> parameter = new HashMap<>();

		// Execute SQL statement
		dynCmd.accept(parameter);
	}

	/**
	 * Create a new root table in SQL database
	 */
	protected void createRootTable() {
		// SQL command
		String sqlCommandString = "CREATE TABLE IF NOT EXISTS elements." + getSqlTableID()
				+ " (NextElementID int, ElementPrefix varchar(255));";
		DynamicSQLUpdate dynCmd = new DynamicSQLUpdate(getDriver(), sqlCommandString);
		
		// Parameter for SQL command statement
		Map<String, Object> parameter = new HashMap<>();

		// Execute SQL statement
		dynCmd.accept(parameter);
		
		Map<String, Object> currentPointer = readCurrentElementPointer();
		
		if (!currentPointer.containsKey("NextElementID")) {
			// Initially fill table if it is empty
			String sqlInsertString = "INSERT INTO elements." + getSqlTableID()
					+ " (NextElementID, ElementPrefix) VALUES (1, '" + getSqlTableID() + ":')";
			DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(getDriver(), sqlInsertString);

			// Clear parameter
			parameter.clear();

			// Run SQL operation
			dynUpdate.accept(parameter);
		}
	}

	
	
	/**
	 * Create a new map element table in SQL database
	 */
	public SQLMap createMap(int elementID) {
		// SQL command
		String sqlCommandString = "CREATE TABLE IF NOT EXISTS elements." + getSqlTableID() + "__" + elementID
				+ " (name text, type int, value text);";
		DynamicSQLUpdate dynCmd = new DynamicSQLUpdate(getDriver(), sqlCommandString);
		
		// Parameter for SQL command statement
		Map<String, Object> parameter = new HashMap<>();

		// Execute SQL statement
		dynCmd.accept(parameter);
		
		// Return created map
		return new SQLMap(this, elementID);
	}

	
	/**
	 * Create a new collection element table in SQL database
	 */
	public SQLCollection createCollection(int elementID) {
		// SQL command
		String sqlCommandString = "CREATE TABLE IF NOT EXISTS elements." + getSqlTableID() + "__" + elementID
				+ " (type int, value text);";
		DynamicSQLUpdate dynCmd = new DynamicSQLUpdate(getDriver(), sqlCommandString);
		
		// Parameter for SQL command statement
		Map<String, Object> parameter = new HashMap<>();

		// Execute SQL statement
		dynCmd.accept(parameter);
		
		// Return created collection
		return new SQLCollection(this, elementID);
	}

	/**
	 * Gets all table names contained in this root element
	 */
	@SuppressWarnings("unchecked")
	private Set<String> getContainedTables() {
		// SQL query string
		String queryString = "SELECT table_name FROM information_schema.tables "
				+ "WHERE table_type = 'BASE TABLE' AND table_schema = 'elements' "
				+ "AND table_name LIKE '" + getSqlTableID() + "__%';";
		DynamicSQLQuery dynQuery = new DynamicSQLQuery(getDriver(), queryString,
				"stringArray(table_name:String)");
		
		// Get table row using no parameters
		Collection<String> tableNames = (Collection<String>) dynQuery.get(new HashMap<>());
		return tableNames.stream().map(name -> "elements." + name).collect(Collectors.toSet());
	}
	
	/**
	 * Drop the root table and remove all contained elements
	 */
	protected void dropRootTable() {
		// Get all tables that belong to this root element
		Set<String> containedTables = getContainedTables();
		containedTables.add("elements." + getSqlTableID());

		// SQL command
		String sqlCommandString = "DROP TABLE IF EXISTS " + String.join(",", containedTables) + ";";
		DynamicSQLUpdate dynCmd = new DynamicSQLUpdate(getDriver(), sqlCommandString);
		// Execute SQL statement without parameters
		dynCmd.accept(new HashMap<>());
	}
	
	
	/**
	 * Drop a root table
	 */
	public void dropTable(int elementID) {
		// SQL command
		String sqlCommandString = "DROP TABLE IF EXISTS elements." + getSqlTableID() + "__" + elementID + ";";
		DynamicSQLUpdate dynCmd = new DynamicSQLUpdate(getDriver(), sqlCommandString);
		
		// Parameter for SQL command statement
		Map<String, Object> parameter = new HashMap<>();

		// Execute SQL statement
		dynCmd.accept(parameter);		
	}

	/**
	 * Creates a new root map, if it does not exist. Otherwise, returns
	 * the first map within this root element.
	 * 
	 * @return
	 */
	public SQLMap retrieveRootMap() {
		Map<String, Object> currentPointer = readCurrentElementPointer();
		int elementId = (int) currentPointer.get("NextElementID");
		if (elementId == 1) {
			// No element has been created, yet => create new root map
			return createMap(getNextIdentifier());
		} else {
			// Root map already exists => return first
			return new SQLMap(this, 1);
		}
	}
}

