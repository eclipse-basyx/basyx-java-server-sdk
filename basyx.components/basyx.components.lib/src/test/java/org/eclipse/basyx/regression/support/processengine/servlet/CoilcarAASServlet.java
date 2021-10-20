/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.processengine.servlet;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.regression.support.processengine.aas.DeviceAdministrationShellFactory;
import org.eclipse.basyx.regression.support.processengine.stubs.Coilcar;
import org.eclipse.basyx.regression.support.processengine.submodel.DeviceSubmodelFactory;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * Servlet for device aas
 * 
 * @author zhangzai
 *
 */
public class CoilcarAASServlet extends VABHTTPInterface<MultiSubmodelProvider> {
	private static final long serialVersionUID = 1L;
	private String aasid = "coilcar";
	private String submodelid = "submodel1";

	public CoilcarAASServlet() {
		super(new MultiSubmodelProvider());

		// Create the aas
		AssetAdministrationShell coilcarAAS = new DeviceAdministrationShellFactory().create(aasid, submodelid);

		// Set aas Id
		coilcarAAS.setIdShort(aasid);

		// Create the sub-model
		Submodel coilcarSubmodel = new DeviceSubmodelFactory().create(submodelid, new Coilcar());

		getModelProvider().setAssetAdministrationShell(new AASModelProvider(coilcarAAS));
		getModelProvider().addSubmodel(new SubmodelProvider(coilcarSubmodel));
	}

}
