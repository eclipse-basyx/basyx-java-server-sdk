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

import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;

/**
 * Represents a BaSyx http servlet configuration for a BaSyxContext,
 * that can be loaded from a properties file.
 * 
 * @author espen
 *
 */
public class BaSyxContextConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxContext_";

	// Default BaSyx Context configuration
	public static final String DEFAULT_CONTEXTPATH = "basys.sdk";
	public static final String DEFAULT_DOCBASE = System.getProperty("java.io.tmpdir");
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int DEFAULT_PORT = 4000;

	public static final String CONTEXTPATH = "contextPath";
	public static final String DOCBASE = "contextDocPath";
	public static final String HOSTNAME = "contextHostname";
	public static final String PORT = "contextPort";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "context.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_CONTEXT";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(CONTEXTPATH, DEFAULT_CONTEXTPATH);
		defaultProps.put(DOCBASE, DEFAULT_DOCBASE);
		defaultProps.put(HOSTNAME, DEFAULT_HOSTNAME);
		defaultProps.put(PORT, Integer.toString(DEFAULT_PORT));
		return defaultProps;
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxContextConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxContextConfiguration(Map<String, String> values) {
		super(values);
	}

	/**
	 * Constructor with initial configuration - docBasePath and hostname are default values
	 * 
	 * @param port        The port that will be occupied
	 * @param contextPath The subpath for this context
	 */
	public BaSyxContextConfiguration(int port, String contextPath) {
		this();
		setPort(port);
		setContextPath(contextPath);
	}

	/**
	 * Constructor with initial configuration - docBasePath and hostname are default values
	 * 
	 * @param contextPath The subpath for this context
	 * @param docBasePath The local base path for the documents
	 * @param hostname    The hostname
	 * @param port        The port that will be occupied
	 */
	public BaSyxContextConfiguration(String contextPath, String docBasePath, String hostname, int port) {
		this();
		setContextPath(contextPath);
		setDocBasePath(docBasePath);
		setHostname(hostname);
		setPort(port);
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { CONTEXTPATH, DOCBASE, HOSTNAME, PORT };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public BaSyxContext createBaSyxContext() {
		String reqContextPath = getContextPath();
		String reqDocBasePath = getDocBasePath();
		String hostName = getHostname();
		int reqPort = getPort();
		return new BaSyxContext(reqContextPath, reqDocBasePath, hostName, reqPort);
	}

	public String getContextPath() {
		return getProperty(CONTEXTPATH);
	}

	public void setContextPath(String contextPath) {
		setProperty(CONTEXTPATH, VABPathTools.stripSlashes(contextPath));
	}

	public String getDocBasePath() {
		return getProperty(DOCBASE);
	}

	public void setDocBasePath(String docBasePath) {
		setProperty(DOCBASE, docBasePath);
	}

	public String getHostname() {
		return getProperty(HOSTNAME);
	}

	public void setHostname(String hostname) {
		setProperty(HOSTNAME, hostname);
	}

	public int getPort() {
		return Integer.parseInt(getProperty(PORT));
	}

	public void setPort(int port) {
		setProperty(PORT, Integer.toString(port));
	}

	public String getUrl() {
		String contextPath = getContextPath();
		String base = "http://" + getHostname() + ":" + getPort();
		if (contextPath.isEmpty()) {
			return base;
		} else {
			return VABPathTools.concatenatePaths(base, contextPath);
		}
	}
}
