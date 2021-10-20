/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.sql;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.basyx.regression.support.directory.ComponentsTestsuiteDirectory;
import org.eclipse.basyx.regression.support.server.context.ComponentsRegressionContext;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.manager.VABConnectionManager;
import org.eclipse.basyx.vab.modelprovider.VABElementProxy;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;



/**
 * Test SQL invocations
 * 
 * @author kuhn
 *
 */
public class SQLInvocationsTest {

	
	/**
	 * Store HTTP asset administration shell manager backend
	 */
	protected VABConnectionManager connManager = new VABConnectionManager(new ComponentsTestsuiteDirectory(), new HTTPConnectorFactory());

	/** 
	 * Makes sure Tomcat Server is started
	 */
	@ClassRule
	public static AASHTTPServerResource res = new AASHTTPServerResource(new ComponentsRegressionContext());
	
	/**
	 * Test basic queries
	 */
	@SuppressWarnings("unused")
	@Test
	@Ignore //FIXME the SQLTestSubmodel does not contain any operations
	public void test() throws Exception {

		// Connect to sub model "CfgFileTestAAS"
		VABElementProxy connSubmodel = this.connManager.connectToVABElement("SQLTestSubmodel");

		
		// Get property value (1)
		Object value1 = connSubmodel.invokeOperation("/aas/submodels/SQLTestSubmodel/operations/sensorIDForName", "VS_0001");
		
		// Get property value (2)
		Object value2 = connSubmodel.invokeOperation("/aas/submodels/SQLTestSubmodel/operations/sensorIDForName", "VS_0002");

		
		// Call operation that inserts a value into the database
		// - Insert line into table
		connSubmodel.invokeOperation("/aas/submodels/SQLTestSubmodel/operations/addSensorID", "sensorname, sensorid", "'VS_0005', '321'");

		// Get property value (3)
		Object value3 = connSubmodel.invokeOperation("/aas/submodels/SQLTestSubmodel/operations/sensorIDForName", "VS_0005");
		
		
		// Delete property 'VS_0005'
		// - Collection that contains call values
		Collection<String> callValues4 = new LinkedList<>();
		callValues4.add("VS_0005");
		// - Delete sensor from table
		connSubmodel.deleteValue("/aas/submodels/SQLTestSubmodel/properties/sensorNames", callValues4);

		// Get property value (4)
		Object value4 = connSubmodel.invokeOperation("/aas/submodels/SQLTestSubmodel/operations/sensorIDForName", "VS_0005");
	}
}

