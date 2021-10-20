/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.NotFoundException;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxDockerConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.tools.webserviceclient.WebServiceRawClient;
import org.eclipse.basyx.vab.coder.json.metaprotocol.MetaprotocolHandler;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test raw http queries to SQL directory provider.
 * 
 * @author espen
 *
 */
public class ITRegistryRaw {
	private static Logger logger = LoggerFactory.getLogger(ITRegistryRaw.class);

	/**
	 * Serialization
	 */
	private static final GSONTools serializer = new GSONTools(new DefaultTypeFactory());
	private static final MetaprotocolHandler handler = new MetaprotocolHandler();

	/**
	 * Invoke BaSyx service calls via web services
	 */
	private static final WebServiceRawClient client = new WebServiceRawClient();
	private static String registryUrl;

	/**
	 * AASDescriptor to test
	 */
	private static final IIdentifier id1 = new ModelUrn("urn:de.FHG:es.iese:aas:0.98:5:lab:microscope#A-166");
	private static final String endpoint1 = "www.endpoint.de";
	private static final AASDescriptor aasDescriptor1 = new AASDescriptor(id1, endpoint1);
	private static final String serializedDescriptor1 = serializer.serialize(aasDescriptor1);
	private static final IIdentifier id2 = new ModelUrn("urn:de.FHG:es.iese:aas:0.98:5:lab:microscope#A-167");
	private static final String endpoint2 = "www.endpoint2.de";
	private static final String endpoint2b = "www.endpoint2.de";
	private static final AASDescriptor aasDescriptor2 = new AASDescriptor(id2, endpoint2);
	private static final AASDescriptor aasDescriptor2b = new AASDescriptor(id2, endpoint2b);
	private static final String serializedDescriptor2 = serializer.serialize(aasDescriptor2);
	private static final String serializedDescriptor2b = serializer.serialize(aasDescriptor2b);
	private static final IIdentifier idUnknown = new ModelUrn("urn:de.FHG:es.iese:aas:0.98:5:lab:microscope#A-168");
	private static String aasUrl1, aasUrl2, aasUrlUnknown;

	@BeforeClass
	public static void setUpClass() throws UnsupportedEncodingException {
		logger.info("Running integration test...");

		logger.info("Loading servlet configuration");
		// Load the servlet configuration inside of the docker configuration from properties file
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);

		// Load the docker environment configuration from properties file
		logger.info("Loading docker configuration");
		BaSyxDockerConfiguration dockerConfig = new BaSyxDockerConfiguration();
		dockerConfig.loadFromResource(BaSyxDockerConfiguration.DEFAULT_CONFIG_PATH);

		registryUrl = "http://localhost:" + dockerConfig.getHostPort() + contextConfig.getContextPath()
				+ "/api/v1/registry/";
		logger.info("Registry URL for integration test: " + registryUrl);
		aasUrl1 = registryUrl + URLEncoder.encode(id1.getId(), "UTF-8");
		aasUrl2 = registryUrl + URLEncoder.encode(id2.getId(), "UTF-8");
		aasUrlUnknown = registryUrl + URLEncoder.encode(idUnknown.getId(), "UTF-8");

		logger.info("Registry URL for integration test: " + registryUrl);
	}

	@Before
	public void setUp() {
		// Put serialized descriptor to register it
		client.put(aasUrl1, serializedDescriptor1);
		client.put(aasUrl2, serializedDescriptor2);
	}

	@After
	public void tearDown() throws UnsupportedEncodingException {
		// Delete AAS registration
		try {
			client.delete(aasUrl1);			
		} catch(NotFoundException e) {}
		try {
			client.delete(aasUrl2);			
		} catch(NotFoundException e) {}
	}

	/**
	 * Execute test case that test working calls
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetterCalls() {
		// First test - get all locally registered AAS
		{
			// Get all locally registered AAS
			Collection<AASDescriptor> result = getResult(client.get(registryUrl));

			// Check if all AAS are contained in result
			assertEquals(2, result.size());
		}

		// Get a specific AAS (1)
		try {
			// Get a known AAS by its ID
			AASDescriptor result = new AASDescriptor((Map<String, Object>) getResult(client.get(aasUrl1)));

			// Check if all AAS are contained in result
			assertEquals(id1.getId(), result.getIdentifier().getId());
		} catch (Exception e) {
			fail("Get specific AAS test case did throw exception:" + e);
		}

		// Get a specific AAS (2)
		try {
			// Get a known AAS by its ID
			AASDescriptor result = new AASDescriptor((Map<String, Object>) getResult(client.get(aasUrl2)));

			// Check if all AAS are contained in result
			assertEquals(id2.getId(), result.getIdentifier().getId());
		} catch (Exception e) {
			fail("Get specific AAS test case did throw exception:" + e);
		}
	}

	/**
	 * Execute update test case
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateCall() throws UnsupportedEncodingException {
		// Update a specific AAS
		// Update AAS registration
		client.put(aasUrl2, serializedDescriptor2b);

		// Get a known AAS by its ID
		AASDescriptor result = new AASDescriptor((Map<String, Object>) getResult(client.get(aasUrl2)));
		// - Check updated registration
		assertEquals(endpoint2b, result.getFirstEndpoint());
	}

	/**
	 * Execute create/Delete test cases
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateDeleteCall() throws UnsupportedEncodingException {
		// Update a specific AAS
		// Delete AAS registration (make sure tests work also if previous test suite
		// did fail)
		client.delete(aasUrl2);

		// Try to get deleted AAS, has to throw exception
		try {
			getResult(client.get(aasUrl2));
			fail();
		} catch(NotFoundException e) {}

		// Create new AAS registration
		client.put(aasUrl2, serializedDescriptor2);

		// Get a known AAS by its ID
		AASDescriptor result2 = new AASDescriptor((Map<String, Object>) getResult(client.get(aasUrl2)));

		assertEquals(endpoint2, result2.getFirstEndpoint()); // need deep json string compare here

		// Delete AAS registration
		client.delete(aasUrl2);

		// Try to get deleted AAS, has to throw exception
		try {
			getResult(client.get(aasUrl2));
			fail();
		} catch(NotFoundException e) {}
	}

	/**
	 * Execute test case that test non-working calls
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Test(expected = NotFoundException.class)
	public void testNonWorkingCalls() throws UnsupportedEncodingException {
		// Get unknown AAS ID, has to throw exception
		getResult(client.get(aasUrlUnknown));
		fail();
	}

	@SuppressWarnings("unchecked")
	private <T> T getResult(String res) {
		try {
			return (T) handler.deserialize(res);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

}
