/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.aas.mongodb.MongoDBSubmodelAPI;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.operation.Operation;
import org.eclipse.basyx.submodel.restapi.MultiSubmodelElementProvider;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SimpleAASSubmodel;
import org.eclipse.basyx.testsuite.regression.submodel.restapi.SubmodelProviderTest;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.TestsuiteDirectory;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.ConnectorFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestMongoDBSubmodelProvider extends SubmodelProviderTest {
	private VABConnectionManager connManager;

	@BeforeClass
	public static void setUpClass() {
		// just reset the data with this default db configuration
		new MongoDBAASAggregator(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH).reset();
	}

	@Override
	protected VABConnectionManager getConnectionManager() {
		if (connManager == null) {
			connManager = new VABConnectionManager(new TestsuiteDirectory(), new ConnectorFactory() {
				@Override
				protected IModelProvider createProvider(String addr) {
					SimpleNoOpAASSubmodel submodel = new SimpleNoOpAASSubmodel();
					MongoDBSubmodelAPI api = new MongoDBSubmodelAPI("mySubmodelId");
					api.setSubmodel(submodel);
					IModelProvider smProvider = new SubmodelProvider(api);
					// Simple submodel for testing specific mappings for submodels
					return smProvider;
				}
			});
		}
		return connManager;
	}

	/**
	 * Invoking operations are not supported
	 */
	@Override
	@Test
	public void testOperationIdShortsWithKeywords() {
		final String base_path = SMPROVIDER_PATH_PREFIX + MultiSubmodelElementProvider.ELEMENTS + "/keywords/";
		VABElementProxy submodelElement = getConnectionManager().connectToVABElement(submodelAddr);
		for (String keyword : SimpleAASSubmodel.KEYWORDS) {
			Operation op = new Operation();
			op.setIdShort(keyword + "Operation");

			String path = base_path + op.getIdShort();
			submodelElement.setValue(path, op);
			submodelElement.getValue(path);
			submodelElement.deleteValue(path);
		}
	}

	/**
	 * Operations are not supported
	 */
	@Override
	@Test
	public void testDeleteOperation() {
	}

	/**
	 * Operations are not supported
	 */
	@Override
	@Test
	public void testInvokeOperation() {
	}
	
	/**
	 * Operations are not supported
	 */
	@Override
	@Test
	public void testInvokeOperationInCollection() {
	}

	/**
	 * Operations are not supported
	 */
	@Override
	@Test
	public void testInvokeAsync() throws Exception {
	}

	/**
	 * Operations are not supported
	 */
	@Override
	@Test
	public void testInvokeAsyncException() throws Exception {
	}

	/**
	 * Now 4 instead of 8 elements
	 */
	@Override
	@SuppressWarnings("unchecked")
	@Test
	public void testReadSubmodelElements() {
		VABElementProxy submodel = getConnectionManager().connectToVABElement(submodelAddr);
		Collection<Map<String, Object>> set = (Collection<Map<String, Object>>) submodel
				.getValue("/submodel/submodelElements");
		assertEquals(5, set.size());
	}

	/**
	 * Operations are not supported
	 */
	@Override
	@Test
	public void testReadSingleOperation() {
	}

	/**
	 * testReadOperations
	 */
	@Test
	public void testReadOperations() {
	}

}
