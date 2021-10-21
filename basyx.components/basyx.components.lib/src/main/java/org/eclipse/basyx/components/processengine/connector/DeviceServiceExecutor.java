/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.processengine.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.basyx.aas.manager.api.IAssetAdministrationShellManager;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.operation.IOperation;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A service executor that invokes services defined in the administration shells.
 * The service executor is called by the Java-delegate class of the process-engine
 * All necessary parameters are delivered through field injections
 * 
 * @author Zhang, Zai
 * */
public class DeviceServiceExecutor implements IDeviceServiceExecutor {
	
	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DeviceServiceExecutor.class);
	
	protected IAssetAdministrationShellManager manager;
	protected String  serviceName;
	protected String serviceProvider;
	protected String serviceSubmodelId;
	protected List<Object> parameters = new ArrayList<>();
	
	
	public DeviceServiceExecutor(IAssetAdministrationShellManager manager) {
		// set-up the administration shell manager to create connected aas
		this.manager = manager;
	};

	/**
	 * Synchronous invocation the expected service specified by the BPMN-model
	 * */
	@Override
	public Object executeService( String servicename, String serviceProvider, String submodelid, List<Object> params) {
		try {
			// create ids
			IIdentifier aasId = new Identifier(IdentifierType.CUSTOM, serviceProvider);
			IIdentifier smId = new Identifier(IdentifierType.CUSTOM, submodelid);

			// create the submodel of the corresponding aas
			ISubmodel serviceSubmodel = manager.retrieveSubmodel(aasId, smId);

			// navigate to the expected service 
			Map<String, IOperation> operations = serviceSubmodel.getOperations();
			IOperation op = operations.get(servicename);
			
			// invoke the service
			logger.debug("#Service Executor#--Call service: %s with parameter: %s \n", servicename,  params);
			Object position = op.invokeSimple(params.toArray());
			
			return position;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	public String getServiceSubmodelId() {
		return serviceSubmodelId;
	}
	
}
