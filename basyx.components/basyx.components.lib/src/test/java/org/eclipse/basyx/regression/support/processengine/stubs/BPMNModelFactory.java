/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.stubs;

import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;

import java.util.ArrayList;
import java.util.Arrays;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.eclipse.basyx.vab.coder.json.serialization.DefaultTypeFactory;
import org.eclipse.basyx.vab.coder.json.serialization.GSONTools;
/**
 * A Factory that produces a defined BPMN-Model programically
 * 
 * @author zhangzai
 *
 */
public class BPMNModelFactory {
	// Path to the java class invoked by the process engine
	private static final String TASKI_MPL = "org.eclipse.basyx.components.processengine.connector.DeviceServiceDelegate";
	

	// id of the submodel
	private static final String SUBMODEL_ID = "submodelId";
	private static final String SERVICE_NAME = "serviceName";
	private static final String SERVICE_PROVIDER = "serviceProvider";
	private static final String SERVICE_PARAMETERS = "serviceParameter";
	
	/**
	 * Create the BPMN-Model and -process with specified process-ID and path to the
	 * java-delegate
	 * 
	 * @param processId - Id of the process
	 * @return
	 */
	public BpmnModelInstance create(String processId) {
	    
		// Create the BPMN Model
		BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("my-process")
	  	      .name(processId)
				.startEvent("start01")
	  	        .name("pickup the coil")
				.exclusiveGateway("gateway01")
	  			.name("check the current position of the coil")
	  			.condition("Pickup position 1","${coilposition==1}")
	  		.serviceTask("t1")
	  	        .name("pickup the coil")
	  	        .camundaClass(TASKI_MPL)
	  		.serviceTask("t3")
	  			.name("drive the coil to the milling machine")
	  			.camundaClass(TASKI_MPL)
	  		.serviceTask("t4")
	  			.name("lift the coil to the expected position")
	  			.camundaClass(TASKI_MPL)
	  		.serviceTask("t5")
	  			.name("put the coil on the mandrel")
	  			.camundaClass(TASKI_MPL)
	  		.serviceTask("t6")
	  			.name("set the lifter to the start position")
	  			.camundaClass(TASKI_MPL)
	  		.serviceTask("t7")
	  			.name("drive the coilcar back to the start position")
	  			.camundaClass(TASKI_MPL)
				.endEvent("end01")
	  			.moveToLastGateway()
	  			.condition("Pickup position 2"," ${coilposition==2}")
			.serviceTask("t2")
				.name("move to the coil")
				.camundaClass(TASKI_MPL)
				.connectTo("t1")
	  		.done();
	    
	    //Add field extensions to service tasks
	    createFieldExtension( modelInstance, "liftTo", "coilcar", new Object[]{1}, SUBMODEL_ID, "t1");
	    createFieldExtension( modelInstance, "moveTo", "coilcar", new Object[]{1}, SUBMODEL_ID, "t2");
	    createFieldExtension( modelInstance, "moveTo", "coilcar", new Object[]{5}, SUBMODEL_ID, "t3");
	    createFieldExtension( modelInstance, "liftTo", "coilcar", new Object[]{6}, SUBMODEL_ID, "t4");
	    createFieldExtension( modelInstance, "moveTo", "coilcar", new Object[]{6}, SUBMODEL_ID, "t5");
	    createFieldExtension( modelInstance, "liftTo", "coilcar", new Object[]{0}, SUBMODEL_ID, "t6");
	    createFieldExtension( modelInstance, "moveTo", "coilcar", new Object[]{0}, SUBMODEL_ID, "t7");
	    
	    return modelInstance;
	}
	

    

	
	/***
	 * Create field extension. This is used to exchange data between a java-delegate and a bpmn-model
	 * @param fname	-- Name of the field
	 * @param fexpression	-- value of the field
	 * @return
	 */
	private void createFieldExtension(BpmnModelInstance modelInstance, String serviceNameValue, String serviceProviderValue, Object[] params, String smIDValue, String taskid) {
		ExtensionElements extensionElements = modelInstance.newInstance(ExtensionElements.class);

		// Create the field serviceName
		ModelElementInstance serviceName = extensionElements.addExtensionElement(CAMUNDA_NS, "field");

		serviceName.setAttributeValueNs(CAMUNDA_NS, "stringValue", serviceNameValue);
		serviceName.setAttributeValueNs(CAMUNDA_NS, "name", SERVICE_NAME);


		// Create field service Provider
		ModelElementInstance serviceProvider = extensionElements.addExtensionElement(CAMUNDA_NS, "field");
		serviceProvider.setAttributeValueNs(CAMUNDA_NS, "stringValue", serviceProviderValue);
		serviceProvider.setAttributeValueNs(CAMUNDA_NS, "name", SERVICE_PROVIDER);

		// Create the field serviceParameter
		ModelElementInstance serviceParameter = extensionElements.addExtensionElement(CAMUNDA_NS, "field");
		serviceParameter.setAttributeValueNs(CAMUNDA_NS, "stringValue", generateJsonString(params));
		serviceParameter.setAttributeValueNs(CAMUNDA_NS, "name", SERVICE_PARAMETERS);

		// Create the field submodelID
		ModelElementInstance submodelID = extensionElements.addExtensionElement(CAMUNDA_NS, "field");
		submodelID.setAttributeValueNs(CAMUNDA_NS, "stringValue", smIDValue);
		submodelID.setAttributeValueNs(CAMUNDA_NS, "name", SUBMODEL_ID);

		// Add all fields into the task
		modelInstance.getModelElementById(taskid).addChildElement(extensionElements);
	}
	
	/**
	 * Generate JSON String of a parameter array 
	 * @param params
	 * @return
	 */
	private String generateJsonString(Object[] params) {
		// Create serializer using BaSys SDK 
		GSONTools gson = new GSONTools(new DefaultTypeFactory());
		
		// serialize array
		String to = gson.serialize(new ArrayList<Object>(Arrays.asList(params)));
		return to;
	}
}
