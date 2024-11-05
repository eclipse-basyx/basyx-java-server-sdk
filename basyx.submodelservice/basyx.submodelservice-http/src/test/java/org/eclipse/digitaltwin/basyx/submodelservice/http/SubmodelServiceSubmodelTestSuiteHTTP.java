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

package org.eclipse.digitaltwin.basyx.submodelservice.http;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/**
 * Base testsuite for all Submodel Service HTTP tests related to the Submodel
 * 
 * @author fried
 *
 */
public abstract class SubmodelServiceSubmodelTestSuiteHTTP {

	protected abstract String getURL();

	@Test
	public void getSubmodel() throws ParseException, IOException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getURL());
		String submodel = BaSyxHttpTestUtils.getResponseAsString(response);

		String expected = getJSONValueAsString("Submodel.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expected, submodel);
	}

	@Test
	public void getSubmodelMetadata() throws IOException, ParseException {
		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getSubmodelMetadataURL());

		String submodelMetadata = BaSyxHttpTestUtils.getResponseAsString(response);
		String expected = getJSONValueAsString("SubmodelMetadata.json");

		BaSyxHttpTestUtils.assertSameJSONContent(expected, submodelMetadata);
	}

	@Test
	public void getSubmodelMetadata_preservesSubmodel() throws IOException, ParseException {

		String submodelBefore = BaSyxHttpTestUtils.getResponseAsString(BaSyxHttpTestUtils.executeGetOnURL(getURL()));

		CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(getSubmodelMetadataURL());
		assertEquals(HttpStatus.OK.value(), response.getCode());

		String submodelAfter = BaSyxHttpTestUtils.getResponseAsString(BaSyxHttpTestUtils.executeGetOnURL(getURL()));

		BaSyxHttpTestUtils.assertSameJSONContent(submodelBefore, submodelAfter);
	}

	private String getSubmodelMetadataURL() {
		return getURL() + "/$metadata";
	}

	private String getJSONValueAsString(String fileName) throws FileNotFoundException, IOException {
		return BaSyxHttpTestUtils.readJSONStringFromClasspath(fileName);
	}

}
