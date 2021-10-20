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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate;
import org.eclipse.basyx.regression.support.processengine.stubs.BPMNModelFactory;
import org.eclipse.basyx.regression.support.processengine.stubs.DeviceServiceExecutorStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test the BPMN-Model created using Bpmn model API, verify the execution
 * sequence of the tasks
 * 
 * @author zhangzai
 *
 */
public class DynamicActivitiProcessTest {
	
	/**
	 * Process instance to be executed
	 */
	ProcessInstance processInstance;
	
	/**
	 * Deployment of the process engine 
	 */
	Deployment deployment;
	
	/**
	 * The bpmn model instance created by the BPMNModelFactory
	 */
	BpmnModelInstance modelInstance;

	/**
	 * configure the process engine before execution
	 */
	  @Rule
	public ProcessEngineRule camundaRule = new ProcessEngineRule(
			new StandaloneProcessEngineConfiguration()
				.setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000").setJdbcUsername("test").setJdbcPassword("test")
				.setJdbcDriver("org.h2.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE)
					.setJobExecutorActivate(false)
				.buildProcessEngine());
	  
	  /**
	 * Deploy the process
	 */
	  @Before
	  public void deploy() {
		// 1. Build up the model from scratch
		modelInstance = new BPMNModelFactory().create("my-process");
		DeviceServiceDelegate.setDeviceServiceExecutor(new DeviceServiceExecutorStub());
		  
		    
		// 2. Deploy the process to the engine
		deployment = camundaRule.getRepositoryService().createDeployment()
				.addModelInstance("dynamic-model.bpmn", modelInstance).name("Dynamic process deployment").deploy();
	  }
	  
  /**
   * Test the first branch of the BPMN-process, verify the execution sequence
   * 	  
   * @throws Exception
   */
	@Test
	public void testDynamicDeployPath1() throws Exception {

		// 3. Start a process instance
		Map<String, Object> variables = new HashMap<String, Object>();
		// Set pickup position to 1
		variables.put("coilposition", 1);
		processInstance = camundaRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);

		// 4. Check if task is available
		HistoryService history = camundaRule.getHistoryService();

		List<HistoricActivityInstance> activitiInstances = history.createHistoricActivityInstanceQuery().list();

		// Check, if the last executed activities are correct
		String[] expectedSequence = new String[] { "end01", "gateway01", "start01", "t1", "t3", "t4", "t5", "t6", "t7" };
		List<String> expected = Arrays.asList(expectedSequence);

		// find the process instance with id in the history
		Stream<HistoricActivityInstance> activityStream = activitiInstances.stream().filter(b -> b.getProcessInstanceId().equals(processInstance.getId()));
		List<HistoricActivityInstance> activityInstances = activityStream.collect(Collectors.toList());

		// assert 9 activities are executed
		assertEquals(9, activityInstances.size());

		// Compare the history tasks
		activityInstances.forEach(x -> {
			// Get id of the activity
			String id = x.getActivityId();

			// Assert existence and sequence
			assertTrue(expected.contains(id));
			assertEquals(expected.get(activityInstances.indexOf(x)), id);
		});

		// check if process instance is ended
		assertEquals(0, camundaRule.getRuntimeService().createProcessInstanceQuery().count());

		// Clear the history
		history.deleteHistoricProcessInstance(processInstance.getId());
		activitiInstances.forEach(x -> {
			history.deleteHistoricTaskInstance(x.getActivityId());
		});

	}
	  
	@Test
	public void testDynamicDeployPath2() throws Exception {
		// 3. Start a process instance
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("coilposition", 2);
		processInstance = camundaRule.getRuntimeService().startProcessInstanceByKey("my-process", variables);
		// 4. Check if task is available
		HistoryService history = camundaRule.getHistoryService();
		List<HistoricActivityInstance> activitiInstances = history.createHistoricActivityInstanceQuery().list();

		// Check, if the last executed activities are correct
		String[] expectedSequence = new String[] { "end01", "gateway01", "start01", "t1", "t2", "t3", "t4", "t5", "t6", "t7" };
		List<String> expected = Arrays.asList(expectedSequence);

		// find the process instance with id in the history
		Stream<HistoricActivityInstance> activityStream = activitiInstances.stream().filter(b -> b.getProcessInstanceId().equals(processInstance.getId()));
		List<HistoricActivityInstance> activityInstances = activityStream.collect(Collectors.toList());

		// assert 10 activities are executed
		assertEquals(10, activityInstances.size());

		// Compare the history tasks
		activityInstances.forEach(x -> {
			// Get id of the activity
			String id = x.getActivityId();

			// Assert existence and sequence
			assertTrue(expected.contains(id));
			assertEquals(expected.get(activityInstances.indexOf(x)), id);
		});

		// check if process instance is ended
		assertEquals(0, camundaRule.getRuntimeService().createProcessInstanceQuery().count());

	}
  

    
	@After
	public void generateOutputFiles() throws IOException {
		OutputStream output = new FileOutputStream(new File("target/process.bpmn20.xml"));
		Bpmn.writeModelToStream(output, modelInstance);
		output.close();
	}
 
}
