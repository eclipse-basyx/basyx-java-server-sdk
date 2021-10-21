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

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.tools.sql.driver.ISQLDriver;
import org.eclipse.basyx.tools.sql.query.DynamicSQLQuery;
import org.eclipse.basyx.tools.sql.query.DynamicSQLUpdate;



/**
 * This class implements a collection that mirrors its contents into a SQL database
 * 
 * A SQL table has the following structure:
 * - type:integer | value:text
 *  
 * @author kuhn
 *
 */
public class SQLCollection extends SQLProxy implements Collection<Object> {

	
	
	/**
	 * Constructor
	 * 
	 * @param rootElement SQLRootElement for this element
	 * @param tableId     Table ID of this element in SQL database
	 */
	public SQLCollection(SQLRootElement rootElement, int tableId) {
		// Invoke base constructor
		super(rootElement.getDriver(), rootElement.getSqlTableID() + "__" + tableId, rootElement);
	}

	
	/**
	 * Constructor
	 * 
	 * @param rootElement        SQLRootElement for this element
	 * @param tableIdWithprefix  Table ID of this element in SQL database with prefix
	 */
	public SQLCollection(SQLRootElement rootElement, String tableIdWithprefix) {
		// Invoke base constructor
		super(rootElement.getDriver(), tableIdWithprefix, rootElement);	
	}

	
	
	
	/**
	 * Get number of collection elements
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
	 * Check if collection is empty
	 */
	@Override
	public boolean isEmpty() {
		// Map is empty iff its size equals 0
		return (size() == 0);
	}

	
	/**
	 * Check if collection contains specified element
	 */
	@Override
	public boolean contains(Object value) {
		// Return query, use new driver
		return contains(getDriver(), value);
	}
	
	
	/**
	 * Check if collection contains specified element
	 */
	protected boolean contains(ISQLDriver drv, Object value) {
		// Build query string
		String          queryString = "SELECT * FROM elements." + getSqlTableID() + " WHERE value='$value'";
		// - Build dynamic query
		// - basically, the last parameter is not used here, as getRaw does not post process query results 
		DynamicSQLQuery dynQuery    = new DynamicSQLQuery(drv, queryString, "mapArray(value:String,type:String)");

		// Build query parameter
		Map<String, Object> parameter = new HashMap<>();
		// - Put name in map
		parameter.put("value", SQLTableRow.getValueAsString(value));
		
		// Execute query, get result set
		ResultSet result = dynQuery.getRaw(parameter);

		// Data base table contains key iff result set size > 1
		return (getSize(result) > 0);
	}


	
	/**
	 * Return array iterator
	 */
	@Override @SuppressWarnings("unchecked")
	public Iterator<Object> iterator() {
		// Get values
		List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) getMapColumnRaw(getSqlTableID(), "type", "value");

		// Create iterator interface instance
		Iterator<Object> it = new Iterator<Object>() {

			// Iterator element index
			private int currentIndex = 0;

			// Check if iterator has another element
			@Override
			public boolean hasNext() {
				return currentIndex < sqlResult.size();
			}

			// Increment iterator element
			@Override
			public Object next() {
				// Get result from SQL
				Map<String, Object> singleResult = sqlResult.get(currentIndex++);
				
				// Process result
				return SQLTableRow.getValueFromString(sqlRootElement, (int) singleResult.get("type"), (String) singleResult.get("value"));
			}

			// No support for remove operation
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
		// Return iterator
		return it;
	}

	
	/**
	 * Return collection elements as array
	 */
	@Override @SuppressWarnings("unchecked")
	public Object[] toArray() {
		// Get values
		List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) getMapColumnRaw(getSqlTableID(), "type", "value");
		
		// Create return value
		Object[] result = new Object[sqlResult.size()];
		// - Fill result
		int counter = 0; 
		for (Map<String, Object> singleResult: sqlResult) {
			result[counter++] = (SQLTableRow.getValueFromString(sqlRootElement, (int) singleResult.get("type"),
					(String) singleResult.get("value")));
		}

		// Return array
		return result;
	}

	
	/**
	 * Return collection elements as array of given type
	 */
	@Override @SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] array) {
		// Get values
		List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) getMapColumnRaw(getSqlTableID(), "type", "value");
		
		// Create return value if necessary
		T[] result = array;
		// - Size check
		if (result.length < sqlResult.size()) result = (T[]) Array.newInstance(array.getClass().getComponentType(), sqlResult.size());

		// Fill result array
		int counter = 0; 
		for (Map<String, Object> singleResult: sqlResult) {
			result[counter++] = (T) (SQLTableRow.getValueFromString(sqlRootElement, (int) singleResult.get("type"), (String) singleResult.get("value")));
		}

		// Return array
		return result;
	}

	
	/**
	 * Add element to collection
	 */
	@Override
	public boolean add(Object value) {
		// Put object
		addToCollectionSimple(getSqlTableID(), new SQLTableRow(value));
		
		// Indicate success
		return true;
	}

	
	/**
	 * Remove element from collection
	 */
	@Override
	public boolean remove(Object value) {
		// Check if key is in map, then update SQL database
		if (contains(getDriver(), value)) {
			// Delete element from map
			String updateString = "DELETE FROM elements." + getSqlTableID() + " WHERE value='$value'";
			DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(getDriver(), updateString);

			// Parameter map
			Map<String, Object> parameter = new HashMap<>();
			// - Put name in map
			parameter.put("value", SQLTableRow.getValueAsString(value));
			// - Execute delete
			dynUpdate.accept(parameter);
	
			// Indicate collection change
			return true;
		}
		
		// No change in collection
		return false;
	}
	
	
	/**
	 * Remove element if element table does contain it
	 */
	protected boolean removeIfContained(ISQLDriver sqlDrv, Object value) {
		// Check if key is in map, then update SQL database
		if (contains(sqlDrv, value)) {
			// Delete element from map
			String updateString = "DELETE FROM elements." + getSqlTableID() + " WHERE value='$value'";
			DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(sqlDrv, updateString);

			// Parameter map
			Map<String, Object> parameter = new HashMap<>();
			// - Put name in map
			parameter.put("value", SQLTableRow.getValueAsString(value));
			// - Execute delete
			dynUpdate.accept(parameter);
	
			// Indicate collection change
			return true;
		}
		
		// No change
		return false;
	}


	/**
	 * Remove element if element table does contain it
	 */
	protected boolean removeSerValueIfContained(ISQLDriver sqlDrv, String value) {
		// Check if key is in map, then update SQL database
		if (contains(sqlDrv, value)) {
			// Delete element from map
			String updateString = "DELETE FROM elements."+ getSqlTableID() +" WHERE value='$value'";
			DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(sqlDrv, updateString);

			// Parameter map
			Map<String, Object> parameter = new HashMap<>();
			// - Put name in map
			parameter.put("value", value);
			// - Execute delete
			dynUpdate.accept(parameter);
	
			// Indicate collection change
			return true;
		}
		
		// No change
		return false;
	}


	/**
	 * Check if collection contains all given elements
	 */
	@Override
	public boolean containsAll(Collection<?> values) {	
		// Flag that indicates if all checked elements are contained in map
		boolean containsAllFlag = true;

		try {
			// Check if all elements are contained 
			for (Object val : values) if (!contains(getDriver(), val)) containsAllFlag = false;

			// Indicate if all requested elements are contained
			return containsAllFlag;
		} catch (Exception e) {
			// Output exception
			e.printStackTrace();
		}

		// An exception did occur
		return false;
	}

	
	/**
	 * Add all elements to collection
	 */
	@Override
	public boolean addAll(Collection<? extends Object> values) {
		// Iterate elements
		for (Object val: values) {
			// Remove element iff contained in SQL table
			addToCollectionSimple(getDriver(), getSqlTableID(), new SQLTableRow(val));
		}

		// Indicate collection change
		return true;
	}

	
	/**
	 * Remove all elements from collection
	 */
	@Override
	public boolean removeAll(Collection<?> values) {
		// Change in SQL database
		boolean performedChange = false;
	
		// Iterate elements
		for (Object val: values) {
			// Remove element iff contained in SQL table
			performedChange |= removeIfContained(getDriver(), val);
		}			

		// Return changed flag
		return performedChange;
	}

	
	/**
	 * Remove all other elements from collection
	 */
	@Override @SuppressWarnings("unchecked")
	public boolean retainAll(Collection<?> values) {
		// Change in SQL database
		boolean performedChange = false;
		
		// Serialize all values in collection
		Collection<String> serValues = new LinkedList<String>();
		// - Serialize values
		for (Object val: values) serValues.add(SQLTableRow.getValueAsString(val));

		// Get all values in table
		List<Map<String, Object>> sqlResult = (List<Map<String, Object>>) getMapColumnRaw(getDriver(), getSqlTableID(), "type", "value");
		
		// Remove all elements that are not part of values collection
		for (Map<String, Object> row: sqlResult) {
			// Remove value if contained in map
			if (!serValues.contains(row.get("value"))) performedChange |= this.removeSerValueIfContained(getDriver(), (String) row.get("value"));
		}

		// Changed flag
		return performedChange;
	}

	
	/**
	 * Clear collection
	 */
	@Override
	public void clear() {
		// Build SQL update string
		String updateString = "DELETE FROM elements."+getSqlTableID();
		DynamicSQLUpdate dynUpdate = new DynamicSQLUpdate(getDriver(), updateString);
		
		// Empty parameter set
		Map<String, Object> parameter = new HashMap<>();
		
		// Run SQL query
		dynUpdate.accept(parameter);	
	}
}

