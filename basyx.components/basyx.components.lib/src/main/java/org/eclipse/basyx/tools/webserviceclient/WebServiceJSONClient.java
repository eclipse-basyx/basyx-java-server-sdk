/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.tools.webserviceclient;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class that supports invocation of remote web services. The class
 * implement serialization/deserialization from and to JSON types
 * 
 * @author kuhn
 *
 */
public class WebServiceJSONClient {
	
	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(WebServiceJSONClient.class);

	/**
	 * Web service raw client instance
	 */
	protected WebServiceRawClient client = new WebServiceRawClient();

	/**
	 * GSON instance
	 */
	protected GSONTools serializer = new GSONTools(new DefaultTypeFactory());

	/**
	 * Get result from webservice invocation
	 * 
	 * @throws ProviderException
	 */
	protected Object getJSONResult(String serializedJSONValue) {
		// Try to deserialize response if any
		try {
			// Try to deserialize response

			Object result = serializer.deserialize(serializedJSONValue);
			// Check if a provider exception was serialized
			if (result instanceof ProviderException) {
				// Throw provider exception
				throw (ProviderException) result;
			}

			// Return deserialized value
			return result;

			// Catch exceptions that did occur during deserialization, return null in this
			// case
		} catch (Exception e) {
			e.printStackTrace();
			// If there is no return value or deserialization failed
			return null;
		}
	}

	/**
	 * Execute a web service, return deserialized object
	 */
	public Object get(String wsURL) {
		// Execute web service call, receive JSON serialized result
		String jsonResult = client.get(wsURL);

		// Return deserialized value
		return getJSONResult(jsonResult);
	}

	/**
	 * Execute a web service put operation, return JSON string
	 */
	public Object put(String wsURL, Object newValue) {
		// Serialize new value to JSON Object
		String json = serializer.serialize(newValue);

		// Execute web service call, receive JSON serialized result
		String jsonResult = client.put(wsURL, json);

		// Return deserialized value
		return getJSONResult(jsonResult);
	}

	/**
	 * Execute a web service post operation, return JSON string
	 */
	public Object post(String wsURL, String... parameter) {
		// Serialize new value to JSON Object
		String json = serializer.serialize(transformArrayToList(parameter));

		// Perform request
		String jsonResult = client.post(wsURL, json);

		logger.debug("Result:" + jsonResult);

		// Return deserialized value
		return getJSONResult(jsonResult);
	}

	/**
	 * Execute a web service patch operation, return JSON string
	 */
	public Object patch(String wsURL, String action, String... parameter) {
		// Serialize new value to JSON Object
		String json = serializer.serialize(transformArrayToList(parameter));

		// Perform request
		String jsonResult = client.patch(wsURL, action, json);

		// Return deserialized value
		return getJSONResult(jsonResult);
	}

	/**
	 * Execute a web service delete operation, return JSON string
	 */
	public Object delete(String wsURL) {
		// Execute web service call, receive JSON serialized result
		String jsonResult = client.delete(wsURL);

		// Return deserialized value
		return getJSONResult(jsonResult);
	}

	private <T> List<T> transformArrayToList(T[] t) {
		List<T> ret = new ArrayList<>();
		for (int i = 0; i < t.length; i++) {
			ret.add(t[i]);
		}
		return ret;
	}
}
