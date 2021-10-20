/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.testsuite.regression.aas.metamodel;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Application for testing an AAS server The first argument is the server host +
 * context-path The application creates an internal registry, then extract the
 * AASs from an existing AASX package. After that, it create the AASs and their
 * sub-models on the server.
 * 
 * @author zhangzai
 *
 */
public class AASServerTestApplication {


	public static void main(String[] args) throws Exception {
		
		// First argument is the inserted url
		String url = args[0];
		AASAggregatorSuiteWithDefinedURL.url = url;



		// Run junit test in a java application
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));

		
		Result result = junit.run(AASAggregatorSuiteWithDefinedURL.class);

		System.out.println("Finished. Result: Failures: " +
				result.getFailureCount() + ". Ignored: " +
				result.getIgnoreCount() + ". Tests run: " +
				result.getRunCount() + ". Time: " +
				result.getRunTime() + "ms.");

	}

}
