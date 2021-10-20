/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.support.server.context;

import org.eclipse.basyx.regression.support.processengine.servlet.CoilcarAASServlet;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;



/**
 * BaSyx context that contains an Industrie 4.0 Servlet infrastructure for regression testing of basys.components package
 * 
 * @author kuhn
 *
 */
public class ComponentsRegressionContext extends BaSyxContext {

	
	/**
	 * Version of serialized instance
	 */
	private static final long serialVersionUID = 1L;


	
	/**
	 * Constructor
	 */
	public ComponentsRegressionContext() {
		// Invoke base constructor to set up Tomcat server in basys.components context
		super("/basys.components", "");
		
		// Define Servlet infrastructure
		addServletMapping("/Testsuite/Processengine/coilcar/*",                   new CoilcarAASServlet());
	}
}

