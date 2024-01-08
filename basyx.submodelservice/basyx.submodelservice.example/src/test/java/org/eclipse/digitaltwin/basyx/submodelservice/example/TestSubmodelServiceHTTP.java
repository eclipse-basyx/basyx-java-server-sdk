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

package org.eclipse.digitaltwin.basyx.submodelservice.example;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Example test showcasing how to test a standalone submodel in BaSyx
 * 
 * @author schnicke
 */
public class TestSubmodelServiceHTTP {
	private static final String SUBMODEL_PATH = "TechnicalSubmodel.json";

	private static ConfigurableApplicationContext appContext;

	private final String accessUrl = "http://localhost:8080/submodel";

	@BeforeAll
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplication(DummySubmodelServiceComponent.class).run(new String[] {});
	}

	@Test
	public void rightSubmodelIsReturnedOnGetRequest() throws IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(accessUrl);
		String actualSubmodel = new BasicHttpClientResponseHandler().handleResponse(response);
		String expectedSubmodel = BaSyxHttpTestUtils.readJSONStringFromClasspath(SUBMODEL_PATH);

		BaSyxHttpTestUtils.assertSameJSONContent(expectedSubmodel, actualSubmodel);
	}

	@AfterAll
	public static void shutdownAASRepo() {
		appContext.close();
	}

}