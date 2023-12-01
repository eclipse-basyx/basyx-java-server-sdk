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

package org.eclipse.digitaltwin.basyx.aasenvironment;

import java.util.Arrays;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.File;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultFile;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodel;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;

/**
 * A helper utility class
 * 
 * @author danish
 */
public class IdShortPathTestHelper {

	public static Submodel createSubmodel(String id) {

		return new DefaultSubmodel.Builder().id(id).idShort("TestSubmodel")
				.submodelElements(Arrays.asList(createScenario1Element().get(0), createScenario2Element().get(0), createScenario3Element().get(0), createScenario4Element().get(0), createScenario5Element().get(0))).build();
	}

	public static Submodel createSubmodelWithMultipleFileElements(String id) {

		return new DefaultSubmodel.Builder().id(id).idShort("TestSubmodel").submodelElements(createScenarioWithMultipleFileElements().get(0).get(0)).build();
	}

	public static List<List<SubmodelElement>> createScenarioWithMultipleFileElements() {

		File fileSME1 = createFileSubmodelElement("File1");
		File fileSME2 = createFileSubmodelElement("File2");
		File fileSME3 = createFileSubmodelElement("File3");

		Property dummyProperty = createPropertySubmodelElement("DummyProperty");

		SubmodelElementCollection smc5 = createSubmodelElementCollection("SMC5");
		smc5.setValue(Arrays.asList(dummyProperty, fileSME3));

		SubmodelElementCollection smc4 = createSubmodelElementCollection("SMC4");
		smc4.setValue(Arrays.asList(smc5, dummyProperty));

		SubmodelElementCollection smc3 = createSubmodelElementCollection("SMC3");
		smc3.setValue(Arrays.asList(fileSME1));

		SubmodelElementCollection smc2 = createSubmodelElementCollection("SMC2");
		smc2.setValue(Arrays.asList(dummyProperty));

		SubmodelElementCollection smc1 = createSubmodelElementCollection("SMC1");
		smc1.setValue(Arrays.asList(dummyProperty, smc2));

		SubmodelElementCollection smc0 = createSubmodelElementCollection("SMC_ROOT");
		smc0.setValue(Arrays.asList(smc1, smc3, fileSME2, smc4, createPropertySubmodelElement("DummyProperty1")));
		
		List<SubmodelElement> fileElementPath1 = Arrays.asList(smc0, smc3, fileSME1);
		List<SubmodelElement> fileElementPath2 = Arrays.asList(smc0, fileSME2);
		List<SubmodelElement> fileElementPath3 = Arrays.asList(smc0, smc4, smc5, fileSME3);
		
		return Arrays.asList(fileElementPath1, fileElementPath2, fileElementPath3);
	}

	public static List<SubmodelElement> createScenario1Element() {

		File fileSME = createFileSubmodelElement("File1_ONE");
		Property dummyProperty = createPropertySubmodelElement("DummyProperty");

		SubmodelElementList sml2 = createSubmodelElementList("SML2");
		sml2.setValue(Arrays.asList(dummyProperty, fileSME));

		SubmodelElementList sml1 = createSubmodelElementList("SML1");
		sml1.setValue(Arrays.asList(sml2));

		SubmodelElementList sml0 = createSubmodelElementList("SML_ONE");
		sml0.setValue(Arrays.asList(createPropertySubmodelElement("DummyProperty1"), createPropertySubmodelElement("DummyProperty2"), sml1));

		return Arrays.asList(sml0, sml1, sml2, fileSME);
	}

	public static List<SubmodelElement> createScenario2Element() {

		File fileSME = createFileSubmodelElement("File1_TWO");
		Property dummyProperty = createPropertySubmodelElement("DummyProperty");

		SubmodelElementCollection smc2 = createSubmodelElementCollection("SMC2");
		smc2.setValue(Arrays.asList(fileSME, dummyProperty));

		SubmodelElementCollection smc1 = createSubmodelElementCollection("SMC1");
		smc1.setValue(Arrays.asList(dummyProperty, smc2));

		SubmodelElementCollection smc0 = createSubmodelElementCollection("SMC_TWO");
		smc0.setValue(Arrays.asList(createPropertySubmodelElement("DummyProperty1"), smc1, createPropertySubmodelElement("DummyProperty2")));

		return Arrays.asList(smc0, smc1, smc2, fileSME);
	}

	public static List<SubmodelElement> createScenario3Element() {

		File fileSME = createFileSubmodelElement("File1_THREE");
		Property dummyProperty = createPropertySubmodelElement("DummyProperty");

		SubmodelElementList sml1 = createSubmodelElementList("SML1");
		sml1.setValue(Arrays.asList(dummyProperty, fileSME));

		SubmodelElementCollection smc1 = createSubmodelElementCollection("SMC1");
		smc1.setValue(Arrays.asList(dummyProperty, sml1));

		SubmodelElementCollection smc0 = createSubmodelElementCollection("SMC0");
		smc0.setValue(Arrays.asList(createPropertySubmodelElement("DummyProperty3"), smc1, createPropertySubmodelElement("DummyProperty4")));

		SubmodelElementList sml0 = createSubmodelElementList("SML_THREE");
		sml0.setValue(Arrays.asList(createPropertySubmodelElement("DummyProperty1"), createPropertySubmodelElement("DummyProperty2"), smc0));

		return Arrays.asList(sml0, smc0, smc1, sml1, fileSME);
	}

	public static List<SubmodelElement> createScenario4Element() {

		File fileSME = createFileSubmodelElement("File1_FOUR");
		Property dummyProperty = createPropertySubmodelElement("DummyProperty");

		SubmodelElementCollection smc1 = createSubmodelElementCollection("SMC1");
		smc1.setValue(Arrays.asList(dummyProperty, fileSME));

		SubmodelElementList sml1 = createSubmodelElementList("SML1");
		sml1.setValue(Arrays.asList(dummyProperty, smc1));

		SubmodelElementList sml0 = createSubmodelElementList("SML0");
		sml0.setValue(Arrays.asList(createPropertySubmodelElement("DummyProperty1"), sml1, createPropertySubmodelElement("DummyProperty2")));

		SubmodelElementCollection smc0 = createSubmodelElementCollection("SMC_FOUR");
		smc0.setValue(Arrays.asList(sml0, createPropertySubmodelElement("DummyProperty3"), createPropertySubmodelElement("DummyProperty4")));

		return Arrays.asList(smc0, sml0, sml1, smc1, fileSME);
	}

	public static List<SubmodelElement> createScenario5Element() {

		File fileSME = createFileSubmodelElement("File1_FIVE");

		return Arrays.asList(fileSME);
	}

	private static SubmodelElementList createSubmodelElementList(String idShort) {

		return new DefaultSubmodelElementList.Builder().idShort(idShort).build();
	}

	private static SubmodelElementCollection createSubmodelElementCollection(String idShort) {

		return new DefaultSubmodelElementCollection.Builder().idShort(idShort).build();
	}

	private static File createFileSubmodelElement(String idShort) {

		return new DefaultFile.Builder().idShort(idShort).build();
	}

	private static Property createPropertySubmodelElement(String idShort) {

		return new DefaultProperty.Builder().idShort(idShort).build();
	}

}
