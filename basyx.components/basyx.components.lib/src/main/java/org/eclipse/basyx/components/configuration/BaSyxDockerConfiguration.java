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
 * Represents a BaSyx docker configuration for a docker environment.
 * 
 * @author espen
 *
 */
public class BaSyxDockerConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxDocker_";

	// Default BaSyx Context configuration
	public static final int DEFAULT_HOSTPORT = 8082;
	public static final int DEFAULT_CONTAINERPORT = 4000;
	public static final String DEFAULT_IMAGENAME = "basys/component";
	public static final String DEFAULT_CONTAINERNAME = "component";

	public static final String HOSTPORT = "BASYX_HOST_PORT";
	public static final String CONTAINERPORT = "BASYX_CONTAINER_PORT";
	public static final String IMAGENAME = "BASYX_IMAGE_NAME";
	public static final String CONTAINERNAME = "BASYX_CONTAINER_NAME";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = ".env";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_DOCKER";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(HOSTPORT, Integer.toString(DEFAULT_HOSTPORT));
		defaultProps.put(CONTAINERPORT, Integer.toString(DEFAULT_CONTAINERPORT));
		defaultProps.put(IMAGENAME, DEFAULT_IMAGENAME);
		defaultProps.put(CONTAINERNAME, DEFAULT_CONTAINERNAME);

		return defaultProps;
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxDockerConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxDockerConfiguration(Map<String, String> values) {
		super(values);
	}

	/**
	 * Constructor with initial configuration
	 * 
	 * @param hostPort      The port for the HOST
	 * @param containerPort The port for the CONTAINER
	 * @param imageName     The name of the image
	 * @param containerName The name of the container
	 */
	public BaSyxDockerConfiguration(int hostPort, int containerPort, String imageName, String containerName) {
		this();
		setHostPort(hostPort);
		setContainerPort(containerPort);
		setImageName(imageName);
		setContainerName(containerName);
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { HOSTPORT, CONTAINERPORT, IMAGENAME, CONTAINERNAME };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public int getHostPort() {
		return Integer.parseInt(getProperty(HOSTPORT));
	}

	public void setHostPort(int hostPort) {
		setProperty(HOSTPORT, Integer.toString(hostPort));
	}

	public int getContainerPort() {
		return Integer.parseInt(getProperty(CONTAINERPORT));
	}

	public void setContainerPort(int containerPort) {
		setProperty(CONTAINERPORT, Integer.toString(containerPort));
	}

	public String getImageName() {
		return getProperty(IMAGENAME);
	}

	public void setImageName(String imageName) {
		setProperty(IMAGENAME, imageName);
	}

	public String getContainerName() {
		return getProperty(CONTAINERNAME);
	}

	public void setContainerName(String containerName) {
		setProperty(CONTAINERNAME, containerName);
	}
}
