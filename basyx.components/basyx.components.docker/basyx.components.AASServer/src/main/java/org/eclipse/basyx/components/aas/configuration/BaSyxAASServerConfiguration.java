/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.components.configuration.BaSyxConfiguration;

import com.google.gson.Gson;

/**
 * Represents a BaSyx server configuration for an AAS Server with any backend,
 * that can be loaded from a properties file.
 * 
 * @author espen
 *
 */
public class BaSyxAASServerConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxAAS_";

	// Default BaSyx AAS configuration
	public static final String DEFAULT_BACKEND = AASServerBackend.INMEMORY.toString();
	public static final String DEFAULT_HOSTPATH = "";
	public static final String DEFAULT_SUBMODELS = "[]";
	public static final String DEFAULT_SOURCE = "";
	public static final String DEFAULT_REGISTRY = "";
	public static final String DEFAULT_EVENTS = AASEventBackend.NONE.toString();
	public static final String DEFAULT_AASX_UPLOAD = AASXUploadBackend.DISABLED.toString();

	// Configuration keys
	public static final String REGISTRY = "registry.path";
	public static final String HOSTPATH = "registry.host";
	public static final String SUBMODELS = "registry.submodels";
	public static final String BACKEND = "aas.backend";
	public static final String SOURCE = "aas.source";
	public static final String EVENTS = "aas.events";
	public static final String AASX_UPLOAD = "aas.aasxUpload";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "aas.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_AAS";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(BACKEND, DEFAULT_BACKEND);
		defaultProps.put(SOURCE, DEFAULT_SOURCE);
		defaultProps.put(REGISTRY, DEFAULT_REGISTRY);
		defaultProps.put(HOSTPATH, DEFAULT_HOSTPATH);
		defaultProps.put(SUBMODELS, DEFAULT_SUBMODELS);
		defaultProps.put(EVENTS, DEFAULT_EVENTS);
		defaultProps.put(AASX_UPLOAD, DEFAULT_AASX_UPLOAD);
		return defaultProps;
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxAASServerConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with initial configuration
	 * 
	 * @param backend
	 *            The backend for the AASServer
	 * @param source
	 *            The file source for the AASServer (e.g. an .aasx file)
	 */
	public BaSyxAASServerConfiguration(AASServerBackend backend, String source) {
		super(getDefaultProperties());
		setAASBackend(backend);
		setAASSource(source);
	}

	/**
	 * Constructor with initial configuration values
	 * 
	 * @param backend
	 *                    The backend for the AASServer
	 * @param source
	 *                    The file source for the AASServer (e.g. an .aasx file)
	 * @param registryUrl
	 *                    The url to the registry
	 */
	public BaSyxAASServerConfiguration(AASServerBackend backend, String source, String registryUrl) {
		this(backend, source);
		setRegistry(registryUrl);
	}

	/**
	 * Constructor with initial configuration values
	 * 
	 * @param backend
	 *            The backend for the AASServer
	 * @param source
	 *            The file source for the AASServer (e.g. an .aasx file)
	 * @param registryUrl
	 *            The url to the registry
	 * @param hostPath
	 *            The root path to the aas component, when it is deployed. Address
	 *            could be used for registration at a registry.
	 */
	public BaSyxAASServerConfiguration(AASServerBackend backend, String source, String registryUrl, String hostPath) {
		this(backend, source, registryUrl);
		setHostpath(hostPath);
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxAASServerConfiguration(Map<String, String> values) {
		super(values);
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { REGISTRY, BACKEND, SOURCE, EVENTS, HOSTPATH, AASX_UPLOAD };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public AASServerBackend getAASBackend() {
		return AASServerBackend.fromString(getProperty(BACKEND));
	}

	public void setAASBackend(AASServerBackend backend) {
		setProperty(BACKEND, backend.toString());
	}

	public AASEventBackend getAASEvents() {
		return AASEventBackend.fromString(getProperty(EVENTS));
	}

	public void setAASEvents(AASEventBackend events) {
		setProperty(EVENTS, events.toString());
	}
	
	public AASXUploadBackend getAASXUpload() {
		return AASXUploadBackend.fromString(getProperty(AASX_UPLOAD));
	}

	public void setAASXUpload(AASXUploadBackend aasxUpload) {
		setProperty(AASX_UPLOAD, aasxUpload.toString());
	}

	public String getAASSource() {
		return getProperty(SOURCE);
	}

	public void setAASSource(String source) {
		setProperty(SOURCE, source);
	}

	public String getRegistry() {
		return getProperty(REGISTRY);
	}

	public void setRegistry(String registryPath) {
		setProperty(REGISTRY, registryPath);
	}

	public List<String> getSubmodels() {
		return parseSubmodels(getProperty(SUBMODELS));
	}

	public void setSubmodels(List<String> submodels) {
		setProperty(SUBMODELS, serializeSubmodels(submodels));
	}

	public String getHostpath() {
		return getProperty(HOSTPATH);
	}

	public void setHostpath(String hostPath) {
		setProperty(HOSTPATH, hostPath);
	}

	@SuppressWarnings("unchecked")
	private List<String> parseSubmodels(String property) {
		List<String> fromJson = new Gson().fromJson(property, List.class);
		if (fromJson == null) {
			return new ArrayList<>();
		} else {
			return fromJson;
		}
	}

	private String serializeSubmodels(List<String> submodels) {
		return new Gson().toJson(submodels);
	}
}
