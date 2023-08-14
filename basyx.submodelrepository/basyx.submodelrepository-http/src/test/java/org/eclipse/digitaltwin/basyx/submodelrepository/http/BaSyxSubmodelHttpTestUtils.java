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

package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.http.Base64UrlEncodedIdentifier;
import org.eclipse.digitaltwin.basyx.http.serialization.BaSyxHttpTestUtils;

/**
 * Supports the tests working with the HTTP/REST API of Submodels
 * 
 * @author schnicke, fischer
 *
 */
public class BaSyxSubmodelHttpTestUtils {
	public static String getSpecificSubmodelAccessPath(String submodelRepoURL, String submodelId) {
		return submodelRepoURL + "/" + Base64UrlEncodedIdentifier.encodeIdentifier(submodelId);
	}

	public static CloseableHttpResponse createSubmodel(String url, String submodelJSON) throws IOException {
		return BaSyxHttpTestUtils.executePostOnURL(url, submodelJSON);
	}

	public static String requestAllSubmodels(String url) throws IOException, ParseException {
		try (CloseableHttpResponse response = BaSyxHttpTestUtils.executeGetOnURL(url)) {
			return BaSyxHttpTestUtils.getResponseAsString(response);	
		}
	}
}