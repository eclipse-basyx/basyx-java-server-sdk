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

import java.util.List;

import org.eclipse.basyx.components.processengine.connector.IDeviceServiceExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeviceServiceExecutorStub implements IDeviceServiceExecutor{
	
	/**
	 * Initiates a logger using the current class
	 */
	private static final Logger logger = LoggerFactory.getLogger(DeviceServiceExecutorStub.class);
	
	private String serviceName;
	private String serviceProvider;
	@SuppressWarnings("unused")
	private String serviceSubmodelid;
	private List<Object> params;
	
	
	// whether the right service is invoked
	@Override
	public Object executeService(String servicename, String serviceProvider, String submodelid,  List<Object> params){
		this.serviceName = servicename;
		this.serviceProvider = serviceProvider;
		this.serviceSubmodelid = submodelid;
		this.params = params;
		logger.debug("service: %s, executed by device: %s , parameters: ", servicename, serviceProvider);
		if (params.size() == 0) {
			logger.debug("[]");
		} else {
			for (Object p : params) {
				logger.debug("%s, ", p);
			}
		}
		
		return 1;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceProvider() {
		return serviceProvider;
	}

	public List<Object> getParams() {
		return params;
	}

}
