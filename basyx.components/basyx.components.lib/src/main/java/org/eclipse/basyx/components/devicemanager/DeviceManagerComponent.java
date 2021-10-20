/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.devicemanager;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.components.service.BaseBaSyxService;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;



/**
 * Base class for device managers
 * 
 * Device managers assume HTTP connection to BaSys infrastructure
 * 
 * Device managers manage devices that are not BaSys conforming by themselves. They:
 * - Register devices with the registry
 * - Receive data from devices and update sub models if necessary
 * - Receive change requests from sub models and update devices
 * 
 * @author kuhn
 *
 */
public abstract class DeviceManagerComponent extends BaseBaSyxService {	
	
	
	/**
	 * Store VAB object ID of default AAS server
	 */
	protected String aasServerObjID = null;
	
	
	/**
	 * Store HTTP URL of AAS server
	 */
	protected String aasServerURL = null;
	

	/**
	 * Set AAS server VAB object ID
	 */
	protected void setAASServerObjectID(String objID) {
		aasServerObjID = objID;
	}
	
	
	/**
	 * Get AAS server VAB object ID
	 */
	protected String getAASServerObjectID() {
		return aasServerObjID;
	}

	
	/**
	 * Set AAS server URL
	 */
	protected void setAASServerURL(String srvUrl) {
		aasServerURL = srvUrl;
	}
	
	
	/**
	 * Get AAS server URL
	 */
	protected String getAASServerURL() {
		return aasServerURL;
	}

	
	/**
	 * Get AAS descriptor for managed device
	 */
	protected abstract AASDescriptor getAASDescriptor();

	/**
	 * Returns the actual endpoint of the AAS managed by this component
	 */
	protected String getAASEndpoint(ModelUrn aasURN) {
		return VABPathTools.concatenatePaths(getAASServerURL(), AASAggregatorProvider.PREFIX, aasURN.getEncodedURN(), "/aas");
	}
	
	/**
	 * Add sub model descriptor to AAS descriptor
	 * 
	 * @param aasDescriptor AAS descriptor of AAS that sub model belongs to
	 * @param subModelURN   URN of sub model that will be described by descriptor
	 * 
	 * @return Sub model descriptor endpoint points to default AAS server location and contains default prefix path
	 */
	protected SubmodelDescriptor addSubmodelDescriptorURI(AASDescriptor aasDescriptor, ModelUrn subModelURN, String subModelId) {
		// Create sub model descriptor
		String submodelEndpoint = VABPathTools.concatenatePaths(getAASServerURL(), AASAggregatorProvider.PREFIX, VABPathTools.encodePathElement(aasDescriptor.getIdentifier().getId()), "/aas/submodels", subModelId);
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(subModelId, subModelURN, submodelEndpoint);
		
		// Add sub model descriptor to AAS descriptor
		aasDescriptor.addSubmodelDescriptor(submodelDescriptor);

		// Return sub model descriptor
		return submodelDescriptor;
	}
}

