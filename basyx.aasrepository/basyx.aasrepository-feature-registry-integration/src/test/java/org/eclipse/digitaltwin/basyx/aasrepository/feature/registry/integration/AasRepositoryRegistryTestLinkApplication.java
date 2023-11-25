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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

/**
 * Application for testing the {@link RegistryIntegrationAasRepository} feature.
 * The first argument is the AAS Repository Base URL, the second argument is the AAS
 * Registry URL.
 * 
 * @author schnicke, danish
 *
 */
public class AasRepositoryRegistryTestLinkApplication {

	public static void main(String[] args) throws Exception {
		String aasRepoBaseUrl = getAasRepositoryBaseUrl(args);
		String aasRegUrl = getAasRegistryUrl(args);

		Result result = runTests(aasRepoBaseUrl, aasRegUrl);

		printResults(result);
	}

	private static void printResults(Result result) {
		System.out.println("Finished. Result: Failures: " + result.getFailureCount() + ". Ignored: " + result.getIgnoreCount() + ". Tests run: " + result.getRunCount() + ". Time: " + result.getRunTime() + "ms.");
	}

	private static Result runTests(String aasRepoBaseUrl, String aasRegUrl) {
		AasRepositoryRegistryTestLink.aasRepoBaseUrl = aasRepoBaseUrl;
		AasRepositoryRegistryTestLink.aasRegistryUrl = aasRegUrl;

		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));

		return junit.run(AasRepositoryRegistryTestLink.class);
	}

	private static String getAasRepositoryBaseUrl(String[] args) {
		return args[0];
	}

	private static String getAasRegistryUrl(String[] args) {
		return args[1];
	}

}
