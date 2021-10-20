/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.servlet.vab;

import java.util.HashMap;

import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * Empty VAB provider servlet. It realizes a VAB lambda provider on its endpoint.
 * 
 * @author kuhn
 *
 */
public class VABLambdaServlet extends VABHTTPInterface<VABLambdaProvider> {

	/**
	 * Version information to identify the version of serialized instances
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public VABLambdaServlet() {
		// Invoke base constructor, instantiate a VAB object
		// - This object initially is empty, it is therefore described by an empty map
		super(new VABLambdaProvider(new HashMap<String, Object>()));
	}
}
