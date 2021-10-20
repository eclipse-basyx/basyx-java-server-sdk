/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.servlet.aas;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * AAS servlet class that exports a given Asset Administration Shell
 * 
 * @author kuhn
 *
 */
public class AASServlet extends VABHTTPInterface<MultiSubmodelProvider> {

	/**
	 * ID of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor - based on a VABMultiSubmodelProvider
	 */
	public AASServlet() {
		super(new MultiSubmodelProvider());
	}

	/**
	 * Constructor for directly creating the AAS in the provider
	 */
	public AASServlet(AssetAdministrationShell exportedAAS) {
		this();
		getModelProvider().setAssetAdministrationShell(new AASModelProvider(exportedAAS));
	}
}
