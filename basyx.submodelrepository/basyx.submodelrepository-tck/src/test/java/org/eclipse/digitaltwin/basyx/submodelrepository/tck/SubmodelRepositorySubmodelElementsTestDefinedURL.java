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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hc.core5.http.ParseException;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.BaSyxSubmodelHttpTestUtils;
import org.eclipse.digitaltwin.basyx.submodelservice.http.SubmodelServiceSubmodelElementsTestSuiteHTTP;
import org.junit.After;
import org.junit.Before;

/**
 * 
 * @author schnicke
 *
 */
public class SubmodelRepositorySubmodelElementsTestDefinedURL extends SubmodelServiceSubmodelElementsTestSuiteHTTP {

	public static String url = "http://localhost:8081/submodels";

	@Before
	public void createSubmodelOnRepo() {
		SubmodelTCKHelper.createSubmodelOnRepository(url, createSubmodel());
	}

	@After
	public void removeSubmodelFromRepo() {
		SubmodelTCKHelper.deleteAllSubmodelsOnRepository(url);
	}

	@Override
	protected String getURL() {
		return BaSyxSubmodelHttpTestUtils.getSpecificSubmodelAccessPath(url, createSubmodel().getId());
	}

	@Override
	public void invokeOperation() throws FileNotFoundException, IOException, ParseException {
		// Not supported on OTS Components for now
	}
}
