/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.support.bundle;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Helper class that supports AASDescriptor utilization for an AASBundle
 * 
 * @author schnicke
 *
 */
public class AASBundleDescriptorFactory {
	/**
	 * Creates the AASDescriptor for the given bundle and hostPath
	 * 
	 * @param bundle
	 * @param hostBasePath
	 * @return
	 */
	public static AASDescriptor createAASDescriptor(AASBundle bundle, String hostBasePath) {
		// Normalize hostBasePath to ensure consistent usage of /
		String nHostBasePath = VABPathTools.stripSlashes(hostBasePath);

		// Create AASDescriptor
		String endpointId = bundle.getAAS().getIdentification().getId();
		endpointId = VABPathTools.encodePathElement(endpointId);
		String aasBase = VABPathTools.concatenatePaths(nHostBasePath, endpointId, "aas");
		AASDescriptor desc = new AASDescriptor(bundle.getAAS(), aasBase);
		bundle.getSubmodels().stream().forEach(s -> {
			SubmodelDescriptor smDesc = new SubmodelDescriptor(s, VABPathTools.concatenatePaths(aasBase, "submodels", s.getIdShort(), "submodel"));
			desc.addSubmodelDescriptor(smDesc);
		});
		return desc;
	}
}
