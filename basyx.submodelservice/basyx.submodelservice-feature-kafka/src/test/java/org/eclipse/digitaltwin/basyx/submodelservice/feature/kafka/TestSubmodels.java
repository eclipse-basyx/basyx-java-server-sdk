/*******************************************************************************
 * Copyright (C) 2024 DFKI GmbH (https://www.dfki.de/en/web)
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
 * 
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.submodelservice.feature.kafka;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
/**
 * @author geso02 (Sonnenberg DFKI GmbH)
 */
public class TestSubmodels {

	public static final String IDSHORT_SM = "sm";
	public static final String IDSHORT_PROP_0 = "prop_0";
	public static final String IDSHORT_PROP_1 = "prop_1";
	public static final String IDSHORT_PROP_TO_BE_REMOVED = "prop_toberemoved";
	public static final String IDSHORT_COLL = "coll";
	public static final String ID_SM = "http://sm.id/0";
	
	private TestSubmodels() {
		
	}
	
	public static Submodel submodel() {
		return TestSubmodels.createSubmodel(ID_SM, IDSHORT_PROP_0, "5");
	}

	public static Submodel createSubmodel(String smId,  String smeId, String value) {
		return new DefaultSubmodel.Builder().id(smId).idShort(IDSHORT_SM)
				.submodelElements(submodelElement(smeId, value))
				.submodelElements(new DefaultSubmodelElementCollection.Builder().idShort(IDSHORT_COLL).build())
				.submodelElements(submodelElement(IDSHORT_PROP_TO_BE_REMOVED, "toBeRemoved"))
				.build();
	}

	public static SubmodelElement submodelElement(String smeId, String value) {
		return new DefaultProperty.Builder().idShort(smeId).value(value).build();
	}

	public static String path(String... idShorts) {
		return String.join(".", idShorts);
	}
}
