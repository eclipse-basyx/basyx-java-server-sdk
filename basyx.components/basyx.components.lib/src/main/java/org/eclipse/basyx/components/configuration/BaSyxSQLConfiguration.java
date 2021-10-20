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
import java.util.Map;

/**
 * Represents a BaSyx sql configuration for a sql connection.
 * 
 * @author espen
 *
 */
public class BaSyxSQLConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxSQL_";

	// Default BaSyx SQL configuration
	public static final String DEFAULT_USER = "postgres";
	public static final String DEFAULT_PASS = "admin";
	public static final String DEFAULT_PATH = "//localhost/basyx-directory?";
	public static final String DEFAULT_DRV = "org.postgresql.Driver";
	public static final String DEFAULT_PREFIX = "jdbc:postgresql:";

	public static final String USER = "dbuser";
	public static final String PASS = "dbpass";
	public static final String PATH = "dburl";
	public static final String DRIVER = "sqlDriver";
	public static final String PREFIX = "sqlPrefix";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "sql.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_SQL";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(USER, DEFAULT_USER);
		defaultProps.put(PASS, DEFAULT_PASS);
		defaultProps.put(PATH, DEFAULT_PATH);
		defaultProps.put(DRIVER, DEFAULT_DRV);
		defaultProps.put(PREFIX, DEFAULT_PREFIX);

		return defaultProps;
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxSQLConfiguration(Map<String, String> values) {
		super(values);
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxSQLConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with initial configuration
	 * 
	 * @param user   Username for SQL database
	 * @param pass   Password for SQL database
	 * @param path   SQL connection path
	 * @param driver SQL driver
	 * @param prefix SQL driver prefix
	 */
	public BaSyxSQLConfiguration(String user, String pass, String path, String driver, String prefix) {
		this();
		setUser(user);
		setPass(pass);
		setPath(path);
		setDriver(driver);
		setPrefix(prefix);
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { USER, PASS, PATH, DRIVER, PREFIX };
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

	public String getPath() {
		return getProperty(PATH);
	}

	public void setPath(String path) {
		setProperty(PATH, path);
	}

	public String getDriver() {
		return getProperty(DRIVER);
	}

	public void setDriver(String driver) {
		setProperty(DRIVER, driver);
	}

	public String getPrefix() {
		return getProperty(PREFIX);
	}

	public void setPrefix(String prefix) {
		setProperty(PREFIX, prefix);
	}
}
