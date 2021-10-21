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
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.AASAggregatorAASXUpload;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.api.IAASAggregatorAASXUpload;
import org.eclipse.basyx.extensions.aas.aggregator.aasxupload.restapi.AASAggregatorAASXUploadProvider;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;

/**
 * A servlet containing the empty infrastructure needed to support receiving
 * AAS/Submodels by clients and hosting them along with the support of uploading
 * AASX via rest api
 * 
 * @author haque
 *
 */
public class AASAggregatorAASXUploadServlet extends VABHTTPInterface<AASAggregatorAASXUploadProvider> {
	private static final long serialVersionUID = -2752423025315116454L;

	public AASAggregatorAASXUploadServlet() {
		super(new AASAggregatorAASXUploadProvider(new AASAggregatorAASXUpload(new AASAggregator())));
	}

	public AASAggregatorAASXUploadServlet(IAASAggregatorAASXUpload aggregator) {
		super(new AASAggregatorAASXUploadProvider(aggregator));
	}
}
