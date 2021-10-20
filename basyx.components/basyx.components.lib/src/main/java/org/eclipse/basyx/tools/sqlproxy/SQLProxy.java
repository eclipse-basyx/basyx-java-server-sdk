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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.eclipse.basyx.tools.sql.query.DynamicSQLQuery;
import org.eclipse.basyx.tools.sql.query.DynamicSQLUpdate;



/**
 * Abstract base class for SQL proxy elements that mirror their content in a SQL table
 * 
 * @author kuhn
 *
 */
public abstract class SQLProxy extends SQLConnector {
	

	/**
	 * Root element for SQL database. It enables access to contained elements
	 */
	protected SQLRootElement sqlRootElement = null;
	


	/**
	 * Constructor
	 *
	 * @param driver       SQL Driver to connect with the database
	 * @param tableID     ID of table for this map in database
	 * @param rootTableID ID of root table in database
	 */
	public SQLProxy(ISQLDriver driver, String tableID, SQLRootElement rootElement) {
		// Invoke base constructor
		super(driver, tableID);
		
		// Store SQL root element
		sqlRootElement = rootElement;
	}
	
	
	
	/**
	 * Helper function: calculate result set size
	 */
	protected int getSize(ResultSet set) {
		// Count rows
		// - Rows counter
		int counter = 0;

		// Iterate rows
		try {
			// Iterate rows, increment counter
			while (set.next()) counter++;

			// Return counter
			return counter;
		} catch (SQLException e) {
			// Print stack trace
			e.printStackTrace();
		}
		
		// Signal error
		return -1;
	}
	
	
	/**
	 * Insert an object into the data base
	 * 
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected void addToMapSimple(String mapName, SQLTableRow sqlMapElement) {
		// Execute addToMapSimple operation
		addToMapSimple(getDriver(), mapName, sqlMapElement);
	}

	
	/**
	 * Insert an object into the data base
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected void addToMapSimple(ISQLDriver drv, String mapName, SQLTableRow sqlMapElement) {
		// SQL insert statement
		String updateString = "INSERT INTO elements."+mapName+" (name, value, type) VALUES ('$name', '$value', '$type')";
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(drv, updateString);

		// Parameter for insert statement
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("name", sqlMapElement.getName());
		parameter.put("value", sqlMapElement.getValueAsString());
		parameter.put("type", sqlMapElement.getTypeID());
		
		// Execute SQL statement
		dynUpdate.accept(parameter);
	}

	
	/**
	 * Insert an object into the data base
	 * 
	 * @param drv JDBC driver to be used
	 * @param collectionName Name of collection
	 * @param sqlCollectionElement Collection element
	 */
	protected void addToCollectionSimple(String collectionName, SQLTableRow sqlCollectionElement) {
		// Add element to collection 
		addToCollectionSimple(getDriver(), collectionName, sqlCollectionElement);
	}

	
	/**
	 * Insert an object into the data base
	 * 
	 * @param drv JDBC driver to be used
	 * @param collectionName Name of collection
	 * @param sqlCollectionElement Collection element
	 */
	protected void addToCollectionSimple(ISQLDriver drv, String collectionName, SQLTableRow sqlCollectionElement) {
		// SQL insert statement
		String updateString = "INSERT INTO elements."+collectionName+" (value, type) VALUES ('$value', '$type')";
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(drv, updateString);

		// Parameter for insert statement
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("value", sqlCollectionElement.getValueAsString());
		parameter.put("type", sqlCollectionElement.getTypeID());
		
		// Execute SQL statement
		dynUpdate.accept(parameter);
	}

	
	/**
	 * Insert a collection of SQLMapElements into the database
	 * 
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected void addToMapMultiple(String mapName, Collection<SQLTableRow> values) {
		// Execute operation
		addToMapMultiple(getDriver(), mapName, values);		
	}

	
	/**
	 * Insert a collection of SQLMapElements into the database
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected void addToMapMultiple(ISQLDriver drv, String mapName, Collection<SQLTableRow> values) {
		// Build value variables for SQL insert string
		StringBuffer valueVariables = new StringBuffer();
		boolean      firstEntry     = true;
		// - Add value variables
		for (int i=0; i<values.size(); i++) {
			if (firstEntry) {
				valueVariables.append("('$name"+i+"', '$value"+i+"', '$type"+i+"')"); firstEntry=false;
			} else {
				valueVariables.append(", ('$name"+i+"', '$value"+i+"', '$type"+i+"')");
			}
		}
		
		// Build insert command string
		String updateString = "INSERT INTO elements."+mapName+" (name, value, type) VALUES "+valueVariables.toString();
		// - Create SQL update command
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(drv, updateString);
		
		// Create command parameter
		Map<String, Object> parameter = new HashMap<>();
		// - Fill command parameter
		int counter = 0; for (SQLTableRow value: values) {
			parameter.put("name"+counter, value.getName());
			parameter.put("value"+counter, value.getValueAsString());
			parameter.put("type"+counter, value.getTypeID());
			counter++;
		}
		
		// Run SQL operation
		dynUpdate.accept(parameter);
	}

	
	/**
	 * Update an entry in data base table
	 * 
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected void updateInMapSimple(String mapName, SQLTableRow sqlMapElement) {
		updateInMapSimple(getDriver(), mapName, sqlMapElement);
	}
	
	
	/**
	 * Update an entry in data base table
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected void updateInMapSimple(ISQLDriver drv, String mapName, SQLTableRow sqlMapElement) {
		// SQL update statement
		String updateString = "UPDATE elements."+mapName+" SET value='$value', type='$type' WHERE name='$name'";
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(getDriver(), updateString);
		
		// Parameter for insert statement
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("name", sqlMapElement.getName());
		parameter.put("value", sqlMapElement.getValueAsString());
		parameter.put("type", sqlMapElement.getTypeID());
		
		// Execute SQL statement
		dynUpdate.accept(parameter);
	}

	
	/**
	 * Remove elements with keys from map
	 */
	protected void removeAllKeys(Set<String> keys) {
		removeAllKeys(getDriver(), keys);
	}
	
	
	/**
	 * Remove elements with keys from map
	 */
	protected void removeAllKeys(ISQLDriver drv, Set<String> keys) {
		// Builder for ID list
		StringBuffer idList       = new StringBuffer();
		boolean      firstElement = true;
		
		// Build keys
		for (String key: keys) {
			if (firstElement) {idList.append("'"+key+"'"); firstElement=false;} else idList.append(", '"+key+"'");
		}
		
		// Delete elements from SQL data base
		String updateString = "DELETE FROM elements." + getSqlTableID() + " WHERE name IN ("+idList.toString()+")";
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(drv, updateString);
		
		// Empty parameter map
		Map<String, Object> parameter = new HashMap<>();
		// - Execute delete
		dynUpdate.accept(parameter);	
	}

	
	
	/**
	 * Get an entry from data base table
	 * 
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected Object getValueFromMap(String mapName, String key) {
		return getValueFromMap(getDriver(), mapName, key);
	}
	
	
	/**
	 * Get an entry from data base table
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected Object getValueFromMap(ISQLDriver drv, String mapName, String key) {
		// SQL query string
		String queryString = "SELECT * FROM elements."+mapName+" WHERE name='$name'";
		DynamicSQLQuery dynQuery = new DynamicSQLQuery(drv, queryString, "mapArray(name:String,value:String,type:Integer)");
		
		// Name of requested table row
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("name", key);
		
		// Get table row
		// - Function returns the column as map
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) dynQuery.get(parameter);
		
		// Get and de-serialize table entry
		// - Null value check, in case that entry was not in database
		if (result.get("type") == null) return null;
		// - De-serialize table entry
		return SQLTableRow.getValueFromString(sqlRootElement, Integer.parseInt((String) result.get("type")), (String) result.get("value"));
	}

	
	
	/**
	 * Get an entry from data base table
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected Object getMapRowRaw(String mapName, String key) {
		return getMapRowRaw(getDriver(), mapName, key);
	}
	
	
	/**
	 * Get an entry from data base table
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param sqlMapElement Map element
	 */
	protected Object getMapRowRaw(ISQLDriver drv, String mapName, String key) {
		// SQL query string
		String queryString = "SELECT * FROM elements."+mapName+" WHERE name='$name'";
		DynamicSQLQuery dynQuery = new DynamicSQLQuery(drv, queryString, "mapArray(name:String,value:String,type:Integer)");
		
		// Name of requested table row
		Map<String, Object> parameter = new HashMap<>();
		parameter.put("name", key);
		
		// Return the column as map
		return dynQuery.get(parameter);
	}

	
	
	/**
	 * Get a column from data base table as set
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param columnName Table column name
	 */
	protected Object getSingleMapColumnRaw(String mapName, String columnName) {
		return getSingleMapColumnRaw(getDriver(), mapName, columnName);
	}
	
	
	/**
	 * Get a column from data base table as set
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param columnName Table column name
	 */
	protected Object getSingleMapColumnRaw(ISQLDriver drv, String mapName, String columnName) {
		// SQL query string
		String queryString = "SELECT "+columnName+" FROM elements."+mapName;
		DynamicSQLQuery dynQuery = new DynamicSQLQuery(drv, queryString, "stringSet("+columnName+":String)");
		
		// Empty parameter set
		Map<String, Object> parameter = new HashMap<>();
		// - Execute query, return the column as 
		return dynQuery.get(parameter);
	}

	
	
	/**
	 * Get a column from data base table as array of maps
	 * 
	 * @param mapName Name of map
	 * @param columnName Table column name
	 */
	protected Object getMapColumnRaw(String mapName, String... columnNames) {
		return getMapColumnRaw(getDriver(), mapName, columnNames);
	}
	
	
	/**
	 * Get a column from data base table as array of maps
	 * 
	 * @param drv JDBC driver to be used
	 * @param mapName Name of map
	 * @param columnName Table column name
	 */
	protected Object getMapColumnRaw(ISQLDriver drv, String mapName, String... columnNames) {
		// Builder for ID list
		StringBuffer nameList       = new StringBuffer();
		StringBuffer parameterList  = new StringBuffer();
		boolean      firstElement   = true;
		
		// Build keys
		for (String name: columnNames) {
			if (firstElement) {
				nameList.append(name); parameterList.append(name+":String"); firstElement=false;
			} else {
				nameList.append(", "+name); parameterList.append(", "+name+":String");
			}
		}

		// SQL query string
		String queryString = "SELECT "+nameList+" FROM elements."+mapName;
		DynamicSQLQuery dynQuery = new DynamicSQLQuery(drv, queryString, "listOfMaps("+parameterList.toString()+")");
		
		// Empty parameter set
		Map<String, Object> parameter = new HashMap<>();
		// - Execute query, return the column as 
		return dynQuery.get(parameter);
	}
}
