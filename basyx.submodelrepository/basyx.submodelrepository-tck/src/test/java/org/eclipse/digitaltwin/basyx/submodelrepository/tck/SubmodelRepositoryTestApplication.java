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

package org.eclipse.digitaltwin.basyx.submodelrepository.tck;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import java.io.PrintWriter;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * Application for testing a Submodel Repository. The first argument is the
 * server URL.
 * 
 * @author schnicke
 *
 */
public class SubmodelRepositoryTestApplication {

	public static void main(String[] args) throws Exception {
		String url = getSubmodelRepositoryUrl(args);

		TestExecutionSummary result = runTests(url);

		printResults(result);
	}

	private static void printResults(TestExecutionSummary result) {
		result.printFailuresTo(new PrintWriter(System.out, true));
		System.out.println("Finished. Result: Failures: " + result.getTotalFailureCount() + ". Ignored: " + result.getTestsSkippedCount() + ". Tests run: " + result.getTestsStartedCount() + ". Time: " + (result.getTimeFinished() - result.getTimeStarted()) + "ms.");
	}

	private static TestExecutionSummary runTests(String url) {
		SubmodelRepositorySubmodelTestDefinedURL.url = url;
		SubmodelRepositorySubmodelElementsTestDefinedURL.url = url;

		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request().selectors(selectClass(SubmodelRepositorySubmodelTestDefinedURL.class), selectClass(SubmodelRepositorySubmodelElementsTestDefinedURL.class)).build();
		Launcher launcher = LauncherFactory.create();
		SummaryGeneratingListener listener = new SummaryGeneratingListener();
		launcher.execute(request, listener);

		return listener.getSummary();
	}

	private static String getSubmodelRepositoryUrl(String[] args) {
		return args[0];
	}

}
