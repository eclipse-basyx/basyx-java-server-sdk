/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.servlet;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * A servlet containing the empty infrastructure needed to support receiving
 * AAS/Submodels by clients and hosting them
 * 
 * @author schnicke
 *
 */
public class AASAggregatorServlet extends VABHTTPInterface<AASAggregatorProvider> {
	private static final long serialVersionUID = 1244938902937878401L;

	public AASAggregatorServlet() {
		super(new AASAggregatorProvider(new AASAggregator()));
	}

	public AASAggregatorServlet(IAASAggregator aggregator) {
		super(new AASAggregatorProvider(aggregator));
	}
}
