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

import java.util.List;

/**
 * Interface for the device service executor
 * 
 * @author zhangzai
 *
 */
public interface IDeviceServiceExecutor {

	/**
	 * 
	 * @param servicename     - name of the operation in the aas
	 * @param serviceProvider - raw urn of the device aas
	 * @param submodelid      - id of the sub-model for asscess
	 * @param params          - parameters needed by the operation in list
	 * @return - return number if operation is executed succesfully
	 * @throws Exception
	 */
	public Object executeService( String servicename, String serviceProvider,String submodelid,  List<Object> params) throws Exception;
}
