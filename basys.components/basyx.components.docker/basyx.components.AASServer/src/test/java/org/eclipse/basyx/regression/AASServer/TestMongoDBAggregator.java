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

import java.util.List;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.testsuite.regression.aas.aggregator.AASAggregatorSuite;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


public class TestMongoDBAggregator extends AASAggregatorSuite {

	@Override
	protected IAASAggregator getAggregator() {
		MongoDBAASAggregator aggregator = new MongoDBAASAggregator(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		aggregator.reset();

		return aggregator;
	}

	@Test
	public void testDeleteReachesDatabase() {
		final BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH);
		final MongoClient client = MongoClients.create(config.getConnectionUrl());
		final MongoTemplate mongoOps = new MongoTemplate(client, config.getDatabase());
		final String aasCollection = config.getAASCollection();
		final IAASAggregator aggregator = getAggregator();

		// initial state: no data in the database
		{
			final List<AssetAdministrationShell> data = mongoOps.findAll(AssetAdministrationShell.class, aasCollection);
			assertEquals(0, data.size());
		}

		// if we add one AAS
		{
			aggregator.createAAS(aas1);
		}

		// there should be that single AAS in the database
		{
			final List<AssetAdministrationShell> data = mongoOps.findAll(AssetAdministrationShell.class, aasCollection);
			assertEquals(1, data.size());
			assertEquals(aas1.getIdentification(), data.get(0).getIdentification());
		}

		// if we delete that AAS
		{
			aggregator.deleteAAS(aas1.getIdentification());
		}

		// there should be no AAS in the database
		{
			final List<AssetAdministrationShell> data = mongoOps.findAll(AssetAdministrationShell.class, aasCollection);
			assertEquals(0, data.size());
		}
	}
}
