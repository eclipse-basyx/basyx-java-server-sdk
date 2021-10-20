/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.processengineconnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate;
import org.eclipse.basyx.components.processengine.connector.DeviceServiceExecutor;
import org.eclipse.basyx.regression.support.server.context.ComponentsRegressionContext;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.vab.protocol.http.AASHTTPServerResource;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Test case for executing BPMN-Model using BaSys SDK and Activiti-engine. 
 * The BPMN-process is defined in the file org/activiti/aas/SimpleTransportProcess.bpmn20.xml
 * The process-model is graphically defined by SimpleTransportProcess.bpmn
 * 
 * @author Zhang,Zai
 * */
public class TestTransportProcess_ConfigureEngineProgrammatically {
	
	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(TestTransportProcess_ConfigureEngineProgrammatically.class);
	
	/**
	 * Connection aas manager for creating connected aas
	 * */
	ConnectedAssetAdministrationShellManager manager;
	
	InMemoryRegistry registry;
	
	/**
	 * Id of the device (coilcar) aas
	 */
	private static final String AAS_ID = "coilcar";
	
	/**
	 * Id of the service submodel
	 */
	private static final String SUBMODEL_ID = "submodel1";
	
	/**
	 * path to the bpmn-file
	 */
	private static String relativeConfigPath = "/WebContent/WEB-INF/config/processengine/SimpleTransportProcess.bpmn20.xml";
	
	/**
	 * Makes sure Tomcat Server is started
	 */
	@ClassRule
	public static AASHTTPServerResource res = new AASHTTPServerResource(new ComponentsRegressionContext());

	/**
	 * Creates the manager to be used in the test cases
	 */
	@Before
	public void build() {
		// Create registry for aas
		registry = new InMemoryRegistry();

		// Create aas descriptor with meta-information of the aas
		IIdentifier id = new Identifier(IdentifierType.CUSTOM, AAS_ID);
		AASDescriptor ccDescriptor = new AASDescriptor(id,
				"http://localhost:8080/basys.components/Testsuite/Processengine/coilcar/aas");
		IIdentifier smId = new Identifier(IdentifierType.CUSTOM, SUBMODEL_ID);
		SubmodelDescriptor smDescriptor = new SubmodelDescriptor("submodel1", smId,
				"http://localhost:8080/basys.components/Testsuite/Processengine/coilcar/aas/submodels/"+SUBMODEL_ID);
		ccDescriptor.addSubmodelDescriptor(smDescriptor);
		
		// register the aas
		registry.register(ccDescriptor);
		manager = new ConnectedAssetAdministrationShellManager(registry, new HTTPConnectorFactory());
		
		
	}
	@Test
	public void deploy() throws FileNotFoundException {
		/* create a configuration for the process engine with associated database configuration
	 		For activating timer function, asynchronous executor must be set to true */
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
				.setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000").setJdbcUsername("test").setJdbcPassword("test")
				.setJdbcDriver("org.h2.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
				.setJobExecutorActivate(false);
		
		// create the process engien with defined configuration
		ProcessEngine processEngine = cfg.buildProcessEngine();
		
		// get the name of the process-engine and prints it out in the console
		String pName = processEngine.getName();
		String ver = ProcessEngine.VERSION;
		logger.debug("ProcessEngine [" + pName + "] Version: [" + ver + "]");
		
		// deploy the BPMN-model defined in the xml file on the process engine
		RepositoryService repositoryService = processEngine.getRepositoryService();

		// Add the XML-file with the BPMN-Model to the repository and deploy it
		
		File configFile = new File(System.getProperty("user.dir") + relativeConfigPath);
		repositoryService.createDeployment().addInputStream("SimpleTransportProcess.bpmn20.xml", new FileInputStream(configFile)).deploy();

		// define the variables in a list which are shared through the execution of the BPMN-process
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("coilposition", 2);
		
		// Set the service executor to the java-delegate
		DeviceServiceDelegate.setDeviceServiceExecutor(new DeviceServiceExecutor(manager));
		
		//  Start a process instance
		@SuppressWarnings("unused")
		ProcessInstance processInstance1 = processEngine.getRuntimeService()
				.startProcessInstanceByKey("SimpleTransportProcess", variables);
		
		// close the engine after the process execution
		processEngine.close();
	}
}
