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
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.eclipse.basyx.tools.sql.query.DynamicSQLQuery;
import org.eclipse.basyx.tools.sql.query.DynamicSQLUpdate;



/**
 * This class implements a map that mirrors its contents into a SQL database
 * 
 * - The map represents the root map that is linked into the SQL database as one table
 * - Subsequent maps map to subsequent SQL tables
 * 
 * A SQL table has the following structure:
 * - name:text | type:integer | value:text
 *  
 * @author kuhn
 *
 */
public class SQLMap extends SQLProxy implements Map<String, Object> {
	
	
	/**
	 * Constructor
	 * 
	 * @param rootElement SQLRootElement for this element
	 * @param tableId     Table ID of this element in SQL database
	 */
	public SQLMap(SQLRootElement rootElement, int tableId) {
		// Invoke base constructor
		super(rootElement.getDriver(), rootElement.getSqlTableID() + "__" + tableId, rootElement);	
	}

	
	/**
	 * Constructor
	 * 
	 * @param rootElement        SQLRootElement for this element
	 * @param tableIdWithprefix  Table ID of this element in SQL database with prefix
	 */
	public SQLMap(SQLRootElement rootElement, String tableIdWithprefix) {
		// Invoke base constructor
		super(rootElement.getDriver(), tableIdWithprefix, rootElement);	
	}

	/**
	 * Constructor for creating a new SQLMap from another Map
	 */
	public SQLMap(SQLRootElement rootElement, Map<String, Object> other) {
		this(rootElement, createMapTable(rootElement));

		for (Entry<String, Object> entry : other.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Create a new table for a map and return its generated id (relative to the root element)
	 * 
	 * @return The table id in the root element scope
	 */
	private static int createMapTable(SQLRootElement rootElement) {
		int tableId = rootElement.getNextIdentifier();
		rootElement.createMap(tableId);
		return tableId;
	}

	/**
	 * Get number of map elements
	 */
	@Override
	public int size() {
		// Build query string
		String          queryString = "SELECT * FROM elements."+ getSqlTableID();
		// - Build dynamic query
		// - basically, the last parameter is not used here, as getRaw does not post process query results 
		DynamicSQLQuery dynQuery    = new DynamicSQLQuery(getDriver(), queryString, "mapArray(name:String,value:String,type:String)");
		
		// Execute query and get result set
		ResultSet result = dynQuery.getRaw();

		// Calculate size
		return getSize(result);
	}

	
	
	/**
	 * Check if map is empty
	 */
	@Override
	public boolean isEmpty() {
		// Map is empty iff its size equals 0
		return (size() == 0);
	}

	
	
	/**
	 * Check if map contains the given key (name)
	 */
	@Override
	public boolean containsKey(Object key) {
		// Use new driver for operation
		return containsKey(getDriver(), key);
	}
	
	
	/**
	 * Check if map contains the given key (name)
	 */
	protected boolean containsKey(ISQLDriver drv, Object key) {
		// Build query string
		String          queryString = "SELECT * FROM elements." + getSqlTableID() + " WHERE name='$name'";
		// - Build dynamic query
		// - basically, the last parameter is not used here, as getRaw does not post process query results 
		DynamicSQLQuery dynQuery    = new DynamicSQLQuery(drv, queryString, "mapArray(name:String,value:String,type:String)");

		// Build query parameter
		Map<String, Object> parameter = new HashMap<>();
		// - Put name in map
		parameter.put("name", key);
		
		// Execute query, get result set
		ResultSet result = dynQuery.getRaw(parameter);

		// Data base table contains key iff result set size > 1
		return getSize(result) > 0;
	}


	
	
	/**
	 * Check if map contains the given value
	 */
	@Override
	public boolean containsValue(Object value) {
		// Use new driver for operation
		return containsValue(getDriver(), value);
	}


	
	/**
	 * Check if map contains the given value
	 */
	protected boolean containsValue(ISQLDriver drv, Object value) {
		// Build query string
		String          queryString = "SELECT * FROM elements."+ getSqlTableID() +" WHERE value='$value'";
		// - Build dynamic query
		// - basically, the last parameter is not used here, as getRaw does not post process query results 
		DynamicSQLQuery dynQuery    = new DynamicSQLQuery(drv, queryString, "mapArray(name:String,value:String,type:String)");

		// Build query parameter
		Map<String, Object> parameter = new HashMap<>();
		// - Put name in map
		parameter.put("value", SQLTableRow.getValueAsString(value));
		
		// Execute query, get result set
		ResultSet result = dynQuery.getRaw(parameter);

		// Data base table contains key iff result set size > 1
		return getSize(result) > 0;
	}


	/**
	 * Get value of map element that is identified by given key
	 */
	@Override
	public Object get(Object key) {
		// Get value from SQL database table
		return getValueFromMap(getSqlTableID(), key.toString());
	}

	
	
	/**
	 * Put a key into a map
	 */
	@Override
	public Object put(String key, Object value) {
		putValue(getDriver(), key, value);
		
		// Return inserted object
		return value;
	}

	/**
	 * Puts arbitrary values in the map (even if they already exist). Does not commit the changes
	 * using the SQLDriver.
	 */
	protected void putValue(ISQLDriver sqlDrv, String key, Object value) {
		value = convertToSimpleValue(value);
		putSimpleValue(sqlDrv, key, value);
	}

	/**
	 * Puts simple values to the map (those, that can be directly converted to a table row). Does not commit the
	 * changes using the SQLDriver.
	 */
	private void putSimpleValue(ISQLDriver sqlDrv, String key, Object value) {
		if (containsKey(sqlDrv, key)) {
			updateInMapSimple(sqlDrv, getSqlTableID(), new SQLTableRow(key, value));
		} else {
			addToMapSimple(sqlDrv, getSqlTableID(), new SQLTableRow(key, value));
		}
	}

	/**
	 * Converts arbitrary values to simple ones that can be directly added to the map's table as a single row.
	 * Currently, maps are supported. For arbitrary maps, a new table will be created that can then be referenced in
	 * this map.
	 */
	@SuppressWarnings("unchecked")
	private Object convertToSimpleValue(Object value) {
		// If the value is a map that can not be directly referenced by this root element
		if (value instanceof Map && (!(value instanceof SQLMap)
				|| !(((SQLMap) value).sqlRootElement.getSqlTableID().equals(sqlRootElement.getSqlTableID())))) {
			// Create a new referable SQLMap out of the value in the scope of this root element
			return new SQLMap(sqlRootElement, (Map<String, Object>) value);
		}
		return value;
	}

	/**
	 * Remove element with key from map
	 */
	@Override
	public Object remove(Object key) {
		// Return value
		Object result = null;
	
		// Get element from map for return value
		result = getValueFromMap(getDriver(), getSqlTableID(), key.toString());

		// Delete element from map
		String updateString = "DELETE FROM elements."+getSqlTableID()+" WHERE name='$name'";
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(getDriver(), updateString);

		// Parameter map
		Map<String, Object> parameter = new HashMap<>();
		// - Put name in map
		parameter.put("name", key);
		// - Execute delete
		dynUpdate.accept(parameter);

		// Return element
		return result;
	}

	
	
	/**
	 * Put all elements into map
	 */
	@Override @SuppressWarnings("unchecked")
	public void putAll(Map<? extends String, ? extends Object> map) {
		// Remove old elements
		removeAllKeys(getDriver(), (Set<String>) map.keySet());
	
		// Create map elements
		Collection<SQLTableRow> mapElements = new LinkedList<>();
		// - Fill collection
		for (String key: map.keySet()) {mapElements.add(new SQLTableRow(key, map.get(key)));}
	
		// Add elements to map
		addToMapMultiple(getDriver(), getSqlTableID(), mapElements);
	}

	
	
	/**
	 * Delete all map elements
	 */
	@Override
	public void clear() {
		// Build SQL update string
		String updateString = "DELETE FROM elements."+ getSqlTableID();
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(getDriver(), updateString);
		
		// Empty parameter set
		Map<String, Object> parameter = new HashMap<>();
		
		// Run SQL query
		dynUpdate.accept(parameter);	
	}

	
	
	/**
	 * Get the set of keys from SQL database
	 */
	@Override @SuppressWarnings("unchecked")
	public Set<String> keySet() {
		// Get key set - keys are stored in "name" column in table
		return (Set<String>) getSingleMapColumnRaw(getSqlTableID(), "name");
	}

	
	
	/**
	 * Get map values that are contained in the SQL database
	 */
	@Override @SuppressWarnings("unchecked")
	public Collection<Object> values() {
		// Get values
		List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) getMapColumnRaw(getSqlTableID(), "type", "value");
		
		// Build types
		Collection<Object> result = new LinkedList<Object>();
		// - Fill result
		for (Map<String, Object> singleResult: sqlResult) {
			result.add(SQLTableRow.getValueFromString(sqlRootElement, (int) singleResult.get("type"), (String) singleResult.get("value"))); 
		}
		
		// Return result
		return result;
	}
	
	

	/**
	 * Return map elements as entry sets
	 */
	@Override @SuppressWarnings("unchecked")
	public Set<Entry<String, Object>> entrySet() {
		// Get values
		List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) getMapColumnRaw(getSqlTableID(), "name", "type", "value");
		
		// Build result
		Set<Entry<String, Object>> result = new HashSet<>();
		
		// Fill hash set - iterate result
		for (Map<String, Object> singleResult: sqlResult) {
			// Deserialize value from string
			Object value = SQLTableRow.getValueFromString(sqlRootElement, (int) singleResult.get("type"), (String) singleResult.get("value"));
			
			// Build entry
			Entry<String, Object> resultEntry = new AbstractMap.SimpleEntry<String, Object>((String) singleResult.get("name"), value);
			
			// Add result entry to result
			result.add(resultEntry);
		}
		
		// Return result
		return result;
	}
}
