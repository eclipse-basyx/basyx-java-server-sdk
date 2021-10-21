/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaSyxConfiguration {
	private static Logger logger = LoggerFactory.getLogger(BaSyxConfiguration.class);

	// Properties in this configuration
	private Map<String, String> values;

	/**
	 * Constructor that takes the configuration's default values.
	 * All the keys in the map are the name of the properties that are stored and loaded in this configuration.
	 */
	public BaSyxConfiguration(Map<String, String> defaultValues) {
		this.values = defaultValues;
	}

	public static InputStream getResourceStream(String relativeResourcePath) {
		ClassLoader classLoader = BaSyxConfiguration.class.getClassLoader();
		return classLoader.getResourceAsStream(relativeResourcePath);
	}

	public static String getResourceString(String relativeResourcePath) throws IOException {
		ClassLoader classLoader = BaSyxConfiguration.class.getClassLoader();
		return IOUtils.resourceToString(relativeResourcePath, StandardCharsets.UTF_8, classLoader);
	}

	/**
	 * Load the configuration from a path relative to the current folder
	 * 
	 * @param filePath Path to the resource in the application folder
	 */
	public void loadFromFile(String filePath) {
		try (InputStream fileStream = new FileInputStream(filePath)) {
			logger.info("Loading properties from file '" + filePath + "'");
			loadFromStream(fileStream);
		} catch (FileNotFoundException e) {
			logger.error("Configuration file not found: '" + filePath + "'", e);
		} catch (IOException e) {
			logger.error("Configuration io error: '" + filePath + "'", e);
		}
	}

	/**
	 * Load the configuration from a path relative to the current folder
	 * 
	 * @param fileKey key where the file path to the resource in the application folder
	 */
	public void loadFileOrDefaultResource(String fileKey, String defaultResource) {
		// Try to load property that points to the configuration file (e.g. java -DfileKey=yx.properties [...])
		String configFilePath = System.getProperty(fileKey);
		if ( configFilePath == null || configFilePath.isEmpty() ) {
			// Try to load environment variable that points to the configuration file
			configFilePath = System.getenv(fileKey);
		}

		// Load context configuration
		if (configFilePath != null && !configFilePath.isEmpty()) {
			// file path available? => load configs from file
			loadFromFile(configFilePath);
		} else {
			// fallback: load default configs (by resource)
			loadFromResource(defaultResource);
		}
	}

	/**
	 * Load the configuration from an input stream
	 * 
	 * @param input the input stream containing the properties
	 */
	public void loadFromStream(InputStream input) {
		logger.info("Loading from inputStream: " + input);
		Properties properties = new Properties();
		try {
			properties.load(input);
			loadFromProperties(properties);
		} catch (IOException e) {
			logger.error("Could not load properties", e);
		}
	}

	/**
	 * Load the configuration from a path relative to the current resource folder
	 * 
	 * @param relativeResourcePath Path to the resource in the resource folder. In a maven project, the resources
	 *                             are located at /src/main/resources by default.
	 */
	public void loadFromResource(String relativeResourcePath) {
		logger.info("Loading properties from resource '" + relativeResourcePath + "'");
		try (InputStream resourceStream = getResourceStream(relativeResourcePath)) {
			if (resourceStream == null) {
				logger.info("Could not get resource stream!");
			}
			loadFromStream(resourceStream);
		} catch (FileNotFoundException e) {
			logger.error("Configuration resource not found: '" + relativeResourcePath + "'", e);
		} catch (IOException e) {
			logger.error("Configuration io error: '" + relativeResourcePath + "'", e);
		}
	}

	/**
	 * Load the configuration directly from properties.
	 */
	public void loadFromProperties(Properties properties) {
		for (Object property : properties.keySet()) {
			String propertyName = (String) property;
			String loaded = properties.getProperty(propertyName);
			if (values.containsKey(propertyName)) {
				logger.info(propertyName + ": '" + loaded + "'");
			} else {
				logger.debug(propertyName + ": '" + loaded + "'");
			}
			values.put(propertyName, loaded);
		}
	}

	/**
	 * Method for subclasses to read specific environment variables
	 * 
	 * @param prefix     The prefix of each of the environment variables
	 * @param properties The name of the properties in the config and environment (with prefix)
	 */
	protected void loadFromEnvironmentVariables(String prefix, String... properties) {
		try {
			for (String propName : properties) {
				String result = System.getenv(prefix + propName);
				if (result != null) {
					logger.info("Environment - " + propName + ": " + result);
					setProperty(propName, result);
				}
			}
		} catch (SecurityException e) {
			logger.info("Reading configs from environment is not permitted");
		}
	}

	/**
	 * Sets a property, if it is contained in this configuration
	 * 
	 * @param name  The name of the property
	 * @param value The new value of the property
	 */
	public void setProperty(String name, String value) {
		values.put(name, value);
	}

	/**
	 * Returns all contained properties that begin with a specific prefix
	 * 
	 * @param prefix The filtered prefix (e.g. "aas.")
	 * @return The list of all contained properties that begin with the prefix
	 */
	public List<String> getProperties(String prefix) {
		return values.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList());
	}

	/**
	 * Queries a property
	 */
	public String getProperty(String name) {
		return values.get(name);
	}
}
