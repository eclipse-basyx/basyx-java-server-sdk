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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.tools.sqlproxy.exception.UnknownElementTypeException;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;



/**
 * Represent a SQL element table row
 * 
 * @author kuhn
 *
 */
public class SQLTableRow {

	/**
	 * Type constant: Unknown
	 */
	public static final int TYPE_UNKNOWN = -1;
	
	/**
	 * Type constant: Null
	 */
	public static final int TYPE_NULL = 0;

	
	/**
	 * Type constant: Integer
	 */
	public static final int TYPE_INT = 1;

	
	/**
	 * Type constant: Float
	 */
	public static final int TYPE_FLOAT = 2;

	
	/**
	 * Type constant: Double
	 */
	public static final int TYPE_DOUBLE = 3;

	
	/**
	 * Type constant: Character
	 */
	public static final int TYPE_CHARACTER = 4;

	
	/**
	 * Type constant: String
	 */
	public static final int TYPE_STRING = 5;

	
	/**
	 * Type constant: Boolean
	 */
	public static final int TYPE_BOOLEAN = 6;
	
	
	/**
	 * Type constant: Integer array
	 */
	public static final int TYPE_INTARRAY = 10;

	
	/**
	 * Type constant: Float array
	 */
	public static final int TYPE_FLOATARRAY = 11;

	
	/**
	 * Type constant: Double array
	 */
	public static final int TYPE_DOUBLEARRAY = 12;

	
	/**
	 * Type constant: Character array
	 */
	public static final int TYPE_CHARACTERARRAY = 13;

	
	/**
	 * Type constant: String array
	 */
	public static final int TYPE_STRINGARRAY = 14;

	
	/**
	 * Type constant: Boolean array
	 */
	public static final int TYPE_BOOLEANARRAY = 15;

	
	/**
	 * Type constant: Collection as a reference to a SQLCollection
	 */
	public static final int TYPE_SQLCOLLECTION = 20;

	
	/**
	 * Type constant: Map as a reference to a SQLMap
	 */
	public static final int TYPE_SQLMAP = 21;

	
	/**
	 * Type constant: Exception
	 */
	public static final int TYPE_EXCEPTION = 22;


	/**
	 * Type constant: Collection as a json string
	 */
	public static final int TYPE_GENERICCOLLECTION = 23;


	/**
	 * Type constant: Map as a json string
	 */
	public static final int TYPE_GENERICMAP = 24;

	
	/**
	 * Reference to default json serializer for string based sql storage
	 */
	protected static GSONTools serializer = new GSONTools(new DefaultTypeFactory());

	
	/**
	 * Store name
	 */
	private String entryName;

	
	/**
	 * Store value
	 */
	private Object entryValue;

	
	/**
	 * Store value as String
	 */
	private String entryValueAsString;
	
	
	/**
	 * Store type
	 */
	private int entryType = TYPE_UNKNOWN;
	
	
	/**
	 * Constructor
	 */
	public SQLTableRow(Object value) {
		// Store value
		entryValue  = value;
		entryName   = null;
		
		// Convert value to String
		entryValueAsString = getValueAsString(value);
		
		// Extract type
		entryType = getTypeID(value);
	}

	
	
	/**
	 * Constructor
	 */
	public SQLTableRow(String name, Object value) {
		// Store name and value
		entryName   = name;
		entryValue  = value;
		
		// Convert value to String
		entryValueAsString = getValueAsString(value);
		
		// Extract type
		entryType = getTypeID(value);
	}
	
	
	/**
	 * Constructor
	 */
	public SQLTableRow(SQLRootElement rootElement, String name, int typeId, String valueAsString) {
		// Store name, type ID, and value as String
		entryName          = name;
		entryType          = typeId;
		entryValueAsString = valueAsString;
		
		// Convert type back
		entryValue = getValueFromString(rootElement, typeId, valueAsString);
	}
	
	
	
	/**
	 * Serialize object to String
	 * 
	 * @param object Object to be serialized
	 * @return Object serialized as string
	 */
	protected static String serializeToString(Object object) {
		// Try to serialize
		try {
			// Serialize object to bytes first
			ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
			ObjectOutputStream    outputStream    = new ObjectOutputStream(byteArrayOutput);
			// - Serialize object
			outputStream.writeObject(object);
			
			// Convert object to String
			return Base64.getEncoder().encodeToString(byteArrayOutput.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * Serialize SQL Map to String
	 */
	protected static String serializeSQLMapToString(SQLMap value) {
		return value.getSqlTableID();
	}

	
	
	/**
	 * Serialize SQL Collection to String
	 */
	protected static String serializeSQLCollectionToString(SQLCollection value) {
		return value.getSqlTableID();
	}
	
	
	
	/**
	 * Serialize SQL Map to String
	 */
	protected static String serializeGenericMapToString(Map<?, ?> value) {
		return serializer.serialize(value);
	}

	
	
	/**
	 * Serialize SQL Collection to String
	 */
	protected static String serializeGenericCollectionToString(Collection<?> value) {
		return serializer.serialize(value);
	}

	
	
	/**
	 * Deserialize object from String
	 * 
	 * @param serializedObject Serialized object
	 * @return Deserialized object
	 */
	protected static Object deserializeFromString(String serializedObject) {
		// Decode String into byte array
		byte[] objectInBytes = Base64.getDecoder().decode(serializedObject);
		
		// Deserialize object
		try {
			// Create object input stream
			ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(objectInBytes));
			
			// Deserialize object
			return inputStream.readObject();
		} catch(IOException | ClassNotFoundException e) {
			// Output exception
			e.printStackTrace();
			
			// Return null
			return null;
		}
	}

	
	
	/**
	 * Deserialize SQL Map from String
	 */
	protected static SQLMap deserializeSQLMapFromString(SQLRootElement rootElement, String serializedObject) {
		return new SQLMap(rootElement, serializedObject);
	}

	
	
	/**
	 * Deserialize SQL Collection from String
	 */
	protected static SQLCollection deserializeSQLCollectionFromString(SQLRootElement rootElement, String serializedObject) {
		return new SQLCollection(rootElement, serializedObject);
	}

	
	/**
	 * Deserialize Collection from JSON
	 */
	@SuppressWarnings("unchecked")
	protected static Collection<Object> deserializeGenericCollectionFromString(String serializedObject) {
		return (Collection<Object>) serializer.deserialize(serializedObject);
	}
	
	/**
	 * Deserialize Map from JSON
	 */
	@SuppressWarnings("unchecked")
	protected static Map<String, Object> deserializeGenericMapFromString(String serializedObject) {
		return (Map<String, Object>) serializer.deserialize(serializedObject);
	}

	/**
	 * Get value as string
	 */
	public static String getValueAsString(Object value) {
		int typeId = getTypeID(value);
		
		switch(typeId) {
		// Null
		case(TYPE_NULL):
			return "(null)";
		
		// Primitive types
		case(TYPE_INT):
		case(TYPE_FLOAT):
		case(TYPE_DOUBLE):
		case(TYPE_STRING):
		case(TYPE_BOOLEAN):
		case(TYPE_CHARACTER):
			return value.toString();
		
		// Array types
		case(TYPE_INTARRAY):
		case(TYPE_FLOATARRAY):
		case(TYPE_DOUBLEARRAY):
		case(TYPE_STRINGARRAY):
		case(TYPE_BOOLEANARRAY):
		case(TYPE_CHARACTERARRAY):
			return serializeToString(value);
		
		// Collection and Map types - first check for SQL types, then check for generic types
		case (TYPE_SQLCOLLECTION):
			return serializeSQLCollectionToString((SQLCollection) value);
		case (TYPE_SQLMAP):
			return serializeSQLMapToString((SQLMap) value);
		case (TYPE_GENERICCOLLECTION):
			return serializeGenericCollectionToString((Collection<?>) value);
		case (TYPE_GENERICMAP):
			return serializeGenericMapToString((Map<?, ?>) value);

		// Unknown or unsupported type
		case (TYPE_EXCEPTION):
			// not supported yet
		default:
			throw new UnknownElementTypeException("");
		}
	}
	
	
	/**
	 * Get value from string
	 */
	public static Object getValueFromString(SQLRootElement rootElement, int typeId, String valAsString) {
		switch (typeId) {
		// Null
		case (TYPE_NULL):
			return null;
		
		// Primitive types
		case (TYPE_INT):
			return Integer.parseInt(valAsString);
		case (TYPE_FLOAT):
			return Float.parseFloat(valAsString);
		case (TYPE_DOUBLE):
			return Double.parseDouble(valAsString);
		case (TYPE_STRING):
			return valAsString;
		case (TYPE_BOOLEAN):
			return Boolean.parseBoolean(valAsString);
		case (TYPE_CHARACTER):
			return valAsString.charAt(0);
		
		// Array types
		case (TYPE_INTARRAY):
		case (TYPE_FLOATARRAY):
		case (TYPE_DOUBLEARRAY):
		case (TYPE_STRINGARRAY):
		case (TYPE_BOOLEANARRAY):
		case (TYPE_CHARACTERARRAY):
			return deserializeFromString(valAsString);
		
		// Collection and Map types - first check for SQL types, then check for generic types
		case (TYPE_SQLCOLLECTION):
			return deserializeSQLCollectionFromString(rootElement, valAsString);
		case (TYPE_SQLMAP):
			return deserializeSQLMapFromString(rootElement, valAsString);
		case (TYPE_GENERICCOLLECTION):
			return deserializeGenericCollectionFromString(valAsString);
		case (TYPE_GENERICMAP):
			return deserializeGenericMapFromString(valAsString);

		// Unknown or unsupported type
		case (TYPE_EXCEPTION):
			// not supported yet
		default:
			return null;
		}
	}
	
	
	/**
	 * Get numeric type ID that represents the type
	 */
	protected static int getTypeID(Object value) {
		// Null pointer check
		if (value == null) return TYPE_NULL;
		
		// Check primitive types
		if (value instanceof Integer) return TYPE_INT;
		if (value instanceof Float) return TYPE_FLOAT;
		if (value instanceof Double) return TYPE_DOUBLE;
		if (value instanceof String) return TYPE_STRING;
		if (value instanceof Boolean) return TYPE_BOOLEAN;
		if (value instanceof Character) return TYPE_CHARACTER;
		
		// Check array types
		if (value instanceof int[]) return TYPE_INTARRAY;
		if (value instanceof Integer[]) return TYPE_INTARRAY;
		if (value instanceof float[]) return TYPE_FLOATARRAY;
		if (value instanceof Float[]) return TYPE_FLOATARRAY;
		if (value instanceof double[]) return TYPE_DOUBLEARRAY;
		if (value instanceof Double[]) return TYPE_DOUBLEARRAY;
		if (value instanceof char[]) return TYPE_CHARACTERARRAY;
		if (value instanceof Character[]) return TYPE_CHARACTERARRAY;
		if (value instanceof boolean[]) return TYPE_BOOLEANARRAY;
		if (value instanceof Boolean[]) return TYPE_BOOLEANARRAY;
		if (value instanceof String[]) return TYPE_STRINGARRAY;

		// Complex types
		if (value instanceof SQLMap) return TYPE_SQLMAP;
		if (value instanceof SQLCollection) return TYPE_SQLCOLLECTION;
		if (value instanceof Map) return TYPE_GENERICMAP;
		if (value instanceof Collection) return TYPE_GENERICCOLLECTION;
		if (value instanceof Exception) return TYPE_EXCEPTION;

		// Function is not supported at the moment
		
		// Unknown type
		return TYPE_UNKNOWN;
	}
	
	
	/**
	 * Get entry name
	 */
	public String getName() {
		return entryName;
	}
	
	
	/**
	 * Get entry value
	 */
	public Object getValue() {
		return entryValue;
	}

	
	/**
	 * Get entry value
	 */
	public String getValueAsString() {
		return entryValueAsString;
	}


	/**
	 * Get entry type id
	 */
	public int getTypeID() {
		return entryType;
	}
}
