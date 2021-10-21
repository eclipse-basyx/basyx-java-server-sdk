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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents a BaSyx mqtt configuration for an mqtt connection.
 * 
 * @author espen
 *
 */
public class BaSyxMqttConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxMQTT_";

	// Default BaSyx MQTT configuration
	public static final String DEFAULT_USER = "";
	public static final String DEFAULT_PASS = "";
	public static final String DEFAULT_SERVER = "http://localhost:1883/";
	public static final String DEFAULT_QOS = "1";
	public static final String DEFAULT_PERSISTENCE_TYPE = MqttPersistence.FILE.toString();
	public static final String DEFAULT_PERSISTENCE_PATH = "";

	public static final String USER = "user";
	public static final String PASS = "pass";
	public static final String SERVER = "server";
	public static final String QOS = "qos";
	public static final String PERSISTENCE_TYPE = "persistence";
	public static final String PERSISTENCE_PATH = "persistencepath";
	public static final String WHITELIST_PREFIX = "whitelist.";
	public static final String WHITELIST_ELEMENT_PREFIX = "whitelist.element.";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "mqtt.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_MQTT";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(USER, DEFAULT_USER);
		defaultProps.put(PASS, DEFAULT_PASS);
		defaultProps.put(SERVER, DEFAULT_SERVER);
		defaultProps.put(QOS, DEFAULT_QOS);
		defaultProps.put(PERSISTENCE_TYPE, DEFAULT_PERSISTENCE_TYPE);
		defaultProps.put(PERSISTENCE_PATH, DEFAULT_PERSISTENCE_PATH);
		return defaultProps;
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxMqttConfiguration(Map<String, String> values) {
		super(values);
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxMqttConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with initial configuration
	 * 
	 * @param user   Username for MQTT connection
	 * @param pass   Password for MQTT connection
	 * @param server MQTT broker address
	 * @param qos    MQTT quality of service level
	 */
	public BaSyxMqttConfiguration(String user, String pass, String server, int qos) {
		this();
		setUser(user);
		setPass(pass);
		setServer(server);
		setQoS(qos);
	}

	/**
	 * Load all settings except of the whitelist config part
	 */
	public void loadFromEnvironmentVariables() {
		String[] properties = { USER, PASS, SERVER, QOS };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public String getUser() {
		return getProperty(USER);
	}

	public void setUser(String user) {
		setProperty(USER, user);
	}

	public String getPass() {
		return getProperty(PASS);
	}

	public void setPass(String pass) {
		setProperty(PASS, pass);
	}

	public String getServer() {
		return getProperty(SERVER);
	}

	public void setServer(String server) {
		setProperty(SERVER, server);
	}

	public int getQoS() {
		return Integer.parseInt(getProperty(QOS));
	}

	public void setQoS(int qos) {
		setProperty(QOS, Integer.toString(qos));
	}

	public MqttPersistence getPersistenceType() {
		return MqttPersistence.fromString(getProperty(PERSISTENCE_TYPE));
	}

	public void setPersistenceType(MqttPersistence type) {
		setProperty(PERSISTENCE_TYPE, type.toString());
	}

	public String getPersistencePath() {
		return getProperty(PERSISTENCE_PATH);
	}

	public void setPersistencePath(String filePath) {
		setProperty(PERSISTENCE_PATH, filePath);
	}

	public boolean isWhitelistEnabled(String submodelId) {
		return "true".equals(getProperty(WHITELIST_PREFIX + submodelId));
	}

	public void setWhitelistEnabled(String submodelId, boolean enabled) {
		String propertyName = WHITELIST_PREFIX + submodelId;
		if (enabled) {
			setProperty(propertyName, "true");
		} else {
			setProperty(propertyName, "false");
		}
	}

	public Set<String> getWhitelist(String submodelId) {
		Set<String> whitelist = new HashSet<>();
		String fullPrefix = WHITELIST_ELEMENT_PREFIX + submodelId;
		List<String> properties = getProperties(fullPrefix);
		
		for ( String prop : properties ) {
			if ( getProperty(prop).equals("true") ) {
				// Removes submodel prefix (+ one separator) => whitelist.elements.smid.
				String elementId = prop.substring(fullPrefix.length() + 1);
				whitelist.add(elementId);
			}
		}
		return whitelist;
	}

	public void setWhitelist(String submodelId, List<String> elementIds) {
		String smPrefix = WHITELIST_ELEMENT_PREFIX + submodelId;
		for (String elemId : elementIds) {
			String propName = smPrefix + "." + elemId;
			setProperty(propName, "true");
		}
	}
}
