/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.catalina.servlets.DefaultServlet;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.bundle.AASBundleHelper;
import org.eclipse.basyx.aas.factory.aasx.AASXToMetamodelConverter;
import org.eclipse.basyx.aas.factory.aasx.SubmodelFileEndpointLoader;
import org.eclipse.basyx.aas.factory.json.JSONAASBundleFactory;
import org.eclipse.basyx.aas.factory.xml.XMLAASBundleFactory;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPIFactory;
import org.eclipse.basyx.components.IComponent;
import org.eclipse.basyx.components.aas.aasx.AASXPackageManager;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.aas.mqtt.MqttSubmodelAPIFactory;
import org.eclipse.basyx.components.aas.servlet.AASAggregatorAASXUploadServlet;
import org.eclipse.basyx.components.aas.servlet.AASAggregatorServlet;
import org.eclipse.basyx.components.configuration.BaSyxConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.AASAggregatorAASXUpload;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Component providing an empty AAS server that is able to receive AAS/SMs from
 * remote. It uses the Aggregator API, i.e. AAS should be pushed to
 * ${URL}/shells
 * 
 * @author schnicke, espen
 *
 */
public class AASServerComponent implements IComponent {
	private static Logger logger = LoggerFactory.getLogger(AASServerComponent.class);

	// The server with the servlet that will be created
	private BaSyxHTTPServer server;
	private IAASRegistry registry;

	// Configurations
	private BaSyxContextConfiguration contextConfig;
	private BaSyxAASServerConfiguration aasConfig;
	private BaSyxMongoDBConfiguration mongoDBConfig;
	private BaSyxMqttConfiguration mqttConfig;

	// Initial AASBundle
	protected Collection<AASBundle> aasBundles;
	
	// Watcher for AAS Aggregator functionality
	private boolean isAASXUploadEnabled = false;

	/**
	 * Constructs an empty AAS server using the passed context
	 */
	public AASServerComponent(BaSyxContextConfiguration contextConfig) {
		this.contextConfig = contextConfig;
		this.aasConfig = new BaSyxAASServerConfiguration();
	}

	/**
	 * Constructs an empty AAS server using the passed configuration
	 */
	public AASServerComponent(BaSyxContextConfiguration contextConfig, BaSyxAASServerConfiguration aasConfig) {
		this.contextConfig = contextConfig;
		this.aasConfig = aasConfig;
	}

	/**
	 * Constructs an empty AAS server using the passed configuration
	 */
	public AASServerComponent(BaSyxContextConfiguration contextConfig, BaSyxAASServerConfiguration aasConfig,
			BaSyxMongoDBConfiguration mongoDBConfig) {
		this.contextConfig = contextConfig;
		this.aasConfig = aasConfig;
		this.aasConfig.setAASBackend(AASServerBackend.MONGODB);
		this.mongoDBConfig = mongoDBConfig;
	}

	/**
	 * Sets and enables mqtt connection configuration for this component. Has to be called before the component is
	 * started. Currently only works for InMemory backend.
	 * 
	 * @param configuration
	 */
	public void enableMQTT(BaSyxMqttConfiguration configuration) {
		this.mqttConfig = configuration;
	}

	/**
	 * Disables mqtt configuration. Has to be called before the component is started.
	 */
	public void disableMQTT() {
		this.mqttConfig = null;
	}
	
	/**
	 * Enables AASX upload functionality
	 */
	public void enableAASXUpload() {
		this.isAASXUploadEnabled = true;
	}

	/**
	 * Sets a registry service for registering AAS that are created during startup
	 * 
	 * @param registry
	 */
	public void setRegistry(IAASRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Starts the AASX component at http://${hostName}:${port}/${path}
	 */
	@Override
	public void startComponent() {
		logger.info("Create the server...");
		// Load the aggregator servlet
		createRegistryFromUrl();
		VABHTTPInterface<?> aggregatorServlet = loadAggregatorServlet();

		// Init HTTP context and add an XMLAASServlet according to the configuration
		BaSyxContext context = contextConfig.createBaSyxContext();
		context.addServletMapping("/*", aggregatorServlet);


		// An initial AAS has been loaded from the drive?
		if (aasBundles != null) {
			// 1. Also provide the files
			context.addServletMapping("/files/*", new DefaultServlet());

			// 2. Fix the file paths according to the servlet configuration
			modifyFilePaths(contextConfig.getHostname(), contextConfig.getPort(), contextConfig.getContextPath());

			// 3. Register the initial AAS
			registerEnvironment();
		}

		logger.info("Start the server");
		server = new BaSyxHTTPServer(context);
		server.start();
	}

	/**
	 * Retrieves the URL on which the component is providing its HTTP server
	 * 
	 * @return
	 */
	public String getURL() {
		return contextConfig.getUrl();
	}

	@Override
	public void stopComponent() {
		
		// Remove all AASs/SMs that were registered on startup
		AASBundleHelper.deregister(registry, aasBundles);
		
		server.shutdown();
	}

	private String loadBundleString(String filePath) throws IOException {
		String content;
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			logger.info("Could not find a corresponding file. Loading from default resource.");
			content = BaSyxConfiguration.getResourceString(filePath);
		}
		return content;
	}

	private void loadBundleFromXML(String xmlPath) throws IOException, ParserConfigurationException, SAXException {
		logger.info("Loading aas from xml \"" + xmlPath + "\"");
		String xmlContent = loadBundleString(xmlPath);
		this.aasBundles = new XMLAASBundleFactory(xmlContent).create();
	}

	private void loadBundleFromJSON(String jsonPath) throws IOException {
		logger.info("Loading aas from json \"" + jsonPath + "\"");
		String jsonContent = loadBundleString(jsonPath);
		this.aasBundles = new JSONAASBundleFactory(jsonContent).create();
	}

	private void loadBundleFromAASX(String aasxPath)
			throws IOException, ParserConfigurationException, SAXException, URISyntaxException, InvalidFormatException {
		logger.info("Loading aas from aasx \"" + aasxPath + "\"");

		// Instantiate the aasx package manager
		AASXToMetamodelConverter packageManager = new AASXPackageManager(aasxPath);

		// Unpack the files referenced by the aas
		packageManager.unzipRelatedFiles();

		// Retrieve the aas from the package
		this.aasBundles = packageManager.retrieveAASBundles();
	}

	private VABHTTPInterface<?> loadAggregatorServlet() {
		// Load the initial AAS bundles from given source
		loadAASFromSource(aasConfig.getAASSource());

		// Load the aggregator
		IAASAggregator aggregator = loadAASAggregator();

		// Integrate the loaded bundles into the aggregator
		if (aasBundles != null) {
			AASBundleHelper.integrate(aggregator, aasBundles);
		}

		// Return the servlet for the resulting aggregator
		if (isAASXUploadEnabled) {
			return new AASAggregatorAASXUploadServlet(new AASAggregatorAASXUpload(aggregator));	
		} else {
			return new AASAggregatorServlet(aggregator);
		}
		
	}

	private void loadAASFromSource(String aasSource) {
		if (aasSource.isEmpty()) {
			return;
		}

		try {
			if (aasSource.endsWith(".aasx")) {
				loadBundleFromAASX(aasSource);
			} else if (aasSource.endsWith(".json")) {
				loadBundleFromJSON(aasSource);
			} else if (aasSource.endsWith(".xml")) {
				loadBundleFromXML(aasSource);
			}
		} catch (IOException | ParserConfigurationException | SAXException | URISyntaxException | InvalidFormatException e) {
			logger.error("Could not load initial AAS from source '" + aasSource + "'");
			logger.info("Starting empty server instead");
		}
	}

	/**
	 * Only creates the registry, if it hasn't been set explicitly before
	 */
	private void createRegistryFromUrl() {
		if (this.registry != null) {
			// Do not overwrite an explicitly set registry
			return;
		}
		// Load registry url from config
		String registryUrl = this.aasConfig.getRegistry();
		if (registryUrl != null && !registryUrl.isEmpty()) {
			registry = new AASRegistryProxy(registryUrl);
			logger.info("Registry loaded at \"" + registryUrl + "\"");
		}
	}

	private void registerEnvironment() {
		if (aasConfig.getSubmodels().isEmpty()) {
			registerFullAAS();
		} else {
			registerSubmodelsFromWhitelist();
		}
	}

	private void registerSubmodelsFromWhitelist() {
		logger.info("Register from whitelist");
		List<AASDescriptor> descriptors = registry.lookupAll();
		List<String> smWhitelist = aasConfig.getSubmodels();
		for (String s : smWhitelist) {
			updateSMEndpoint(s, descriptors);
		}
	}

	private void registerFullAAS() {
		if (registry == null) {
			logger.info("No registry specified, skipped registration");
			return;
		}

		String baseUrl = getComponentBasePath();
		String aggregatorPath = VABPathTools.concatenatePaths(baseUrl, AASAggregatorProvider.PREFIX);
		AASBundleHelper.register(registry, aasBundles, aggregatorPath);
	}

	private void updateSMEndpoint(String smId, List<AASDescriptor> descriptors) {
		descriptors.forEach(desc -> {
			Collection<SubmodelDescriptor> smDescriptors = desc.getSubmodelDescriptors();
			SubmodelDescriptor smDescriptor = findSMDescriptor(smId, smDescriptors);
			updateSMEndpoint(smDescriptor);
			registry.register(desc.getIdentifier(), smDescriptor);
		});
	}

	private void updateSMEndpoint(SubmodelDescriptor smDescriptor) {
		String smEndpoint = getSMEndpoint(smDescriptor.getIdentifier());
		String firstEndpoint = smDescriptor.getFirstEndpoint();
		if (firstEndpoint.isEmpty()) {
			smDescriptor.removeEndpoint("");
		} else if (firstEndpoint.equals("/submodel")) {
			smDescriptor.removeEndpoint("/submodel");
		}
		smDescriptor.addEndpoint(smEndpoint);
	}

	private SubmodelDescriptor findSMDescriptor(String smId, Collection<SubmodelDescriptor> smDescriptors) {
		for (SubmodelDescriptor smDesc : smDescriptors) {
			if (smDesc.getIdentifier().getId().equals(smId)) {
				return smDesc;
			}
		}
		return null;
	}

	private String getSMEndpoint(IIdentifier smId) {
		String aasId = getAASIdFromSMId(smId);
		String encodedAASId = VABPathTools.encodePathElement(aasId);
		String aasBasePath = VABPathTools.concatenatePaths(getComponentBasePath(), encodedAASId, "aas");
		String smIdShort = getSMIdShortFromSMId(smId);
		return VABPathTools.concatenatePaths(aasBasePath, "submodels", smIdShort, "submodel");
	}

	private String getSMIdShortFromSMId(IIdentifier smId) {
		for (AASBundle bundle : aasBundles) {
			for (ISubmodel sm : bundle.getSubmodels()) {
				if (smId.getId().equals(sm.getIdentification().getId())) {
					return sm.getIdShort();
				}
			}
		}
		throw new ResourceNotFoundException("Submodel in registry whitelist not found in AASBundle");
	}

	private String getAASIdFromSMId(IIdentifier smId) {
		for (AASBundle bundle : aasBundles) {
			for (ISubmodel sm : bundle.getSubmodels()) {
				if (smId.getId().equals(sm.getIdentification().getId())) {
					return bundle.getAAS().getIdentification().getId();
				}
			}
		}
		throw new ResourceNotFoundException("Submodel in registry whitelist does not belong to any AAS in AASBundle");
	}

	private String getComponentBasePath() {
		String basePath = aasConfig.getHostpath();
		if (basePath.isEmpty()) {
			return contextConfig.getUrl();
		}
		return basePath;
	}

	/**
	 * Fixes the File submodel element value paths according to the given endpoint configuration
	 */
	private void modifyFilePaths(String hostName, int port, String rootPath) {
		rootPath = rootPath + "/files";
		for (AASBundle bundle : aasBundles) {
			Set<ISubmodel> submodels = bundle.getSubmodels();
			for (ISubmodel sm : submodels) {
				SubmodelFileEndpointLoader.setRelativeFileEndpoints(sm, hostName, port, rootPath);
			}
		}
	}

	/**
	 * Loads a aas aggregator servlet with a backend according to the configuration
	 * 
	 * @return
	 */
	private IAASAggregator loadAASAggregator() {
		// Get aggregator according to backend config
		AASServerBackend backendType = aasConfig.getAASBackend();
		IAASAggregator aggregator = null;
		if (backendType == AASServerBackend.INMEMORY && mqttConfig == null) {
			logger.info("Using InMemory backend");
			aggregator = new AASAggregator(registry);
		} else if (backendType == AASServerBackend.INMEMORY && mqttConfig != null) {
			logger.info("Using InMemory backend with MQTT providers");
			IAASAPIFactory aasApiProvider = new VABAASAPIFactory();
			ISubmodelAPIFactory smApiProvider = new MqttSubmodelAPIFactory(mqttConfig);
			aggregator = new AASAggregator(aasApiProvider, smApiProvider, registry);
		} else if ( backendType == AASServerBackend.MONGODB ) {
			logger.info("Using MongoDB backend");
			aggregator = loadMongoDBAggregator();
		}

		return aggregator;
	}

	private IAASAggregator loadMongoDBAggregator() {
		BaSyxMongoDBConfiguration config;
		if (this.mongoDBConfig == null) {
			config = new BaSyxMongoDBConfiguration();
			config.loadFromDefaultSource();
		} else {
			config = this.mongoDBConfig;
		}
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(config);
		aggregator.setRegistry(registry);
		return aggregator;
	}
}
