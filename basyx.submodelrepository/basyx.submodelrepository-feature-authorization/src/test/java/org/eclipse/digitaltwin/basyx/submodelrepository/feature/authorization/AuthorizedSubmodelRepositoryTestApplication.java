/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Application for testing the {@link AuthorizedSubmodelRepository} feature. The
 * first argument is the Submodel Repository Base URL, the second argument is the
 * Authentication server's token endpoint
 * 
 * @author schnicke, danish
 *
 */
public class AuthorizedSubmodelRepositoryTestApplication {

	public static void main(String[] args) throws Exception {
		String submodelRepositoryBaseUrl = getSubmodelRepositoryBaseUrl(args);
		String authenticaltionServerTokenEndpoint = getAuthenticaltionServerTokenEndpoint(args);

		Result result = runTests(submodelRepositoryBaseUrl, authenticaltionServerTokenEndpoint);

		printResults(result);
	}

	private static void printResults(Result result) {
		System.out.println("Finished. Result: Failures: " + result.getFailureCount() + ". Ignored: " + result.getIgnoreCount() + ". Tests run: " + result.getRunCount() + ". Time: " + result.getRunTime() + "ms.");
	}

	private static Result runTests(String submodelRepositoryBaseUrl, String authenticaltionServerTokenEndpoint) {
		AuthorizedSubmodelRepositoryTestSuite.submodelRepositoryBaseUrl = submodelRepositoryBaseUrl;
		AuthorizedSubmodelRepositoryTestSuite.authenticaltionServerTokenEndpoint = authenticaltionServerTokenEndpoint;

		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));

		return junit.run(AuthorizedSubmodelRepositoryTestSuite.class);
	}

	private static String getSubmodelRepositoryBaseUrl(String[] args) {
		return args[0];
	}

	private static String getAuthenticaltionServerTokenEndpoint(String[] args) {
		return args[1];
	}

}
