/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization;

import static org.junit.Assert.*;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidTargetInformationException;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.rbac.backend.submodel.SubmodelTargetInformationAdapter;

/**
 * Tests {@link SubmodelTargetInformationAdapter}
 * 
 * @author danish
 */
public class SubmodelTargetInformationAdapterTest {

	private SubmodelTargetInformationAdapter submodelTargetInformationAdapter;

	@Before
	public void setUp() {
		submodelTargetInformationAdapter = new SubmodelTargetInformationAdapter();
	}

	@Test
	public void testAdaptTargetInformationToSubmodelElementCollection() {

		List<String> submodelIds = Arrays.asList("aasId1", "aasId2");
		List<String> smeIds = Arrays.asList("sme1.sme2", "sme4.sme5.sme6", "sme7");

		TargetInformation targetInformation = new SubmodelTargetInformation(submodelIds, smeIds);

		SubmodelElementCollection result = submodelTargetInformationAdapter.adapt(targetInformation);

		assertEquals("targetInformation", result.getIdShort());

		List<SubmodelElement> elements = result.getValue();
		assertEquals(3, elements.size());

		SubmodelElementList submodelIdList = (SubmodelElementList) elements.get(0);
		assertEquals("submodelIds", submodelIdList.getIdShort());

		SubmodelElementList smeIdList = (SubmodelElementList) elements.get(1);
		assertEquals("submodelElementIdShortPaths", smeIdList.getIdShort());

		Property typeProperty = (Property) elements.get(2);
		assertEquals("@type", typeProperty.getIdShort());

		List<String> actualSubmodelIds = submodelIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
		List<String> actualSmeIds = smeIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
		assertEquals(submodelIds, actualSubmodelIds);
		assertEquals(smeIds, actualSmeIds);

		String actualType = typeProperty.getValue();
		assertTrue(actualType.equals("submodel"));
	}

	@Test
	public void testAdaptSubmodelElementCollectionToTargetInformation() {

		List<String> expectedSubmodelIds = Arrays.asList("aasId1", "aasId2");
		List<String> expectedSmeIds = Arrays.asList("sme1.sme2", "sme4.sme5.sme6", "sme7");
		String type = "submodel";

		List<SubmodelElement> submodelIdProperties = expectedSubmodelIds.stream().map(submodelId -> new DefaultProperty.Builder().value(submodelId).build()).collect(Collectors.toList());
		List<SubmodelElement> smeIdProperties = expectedSmeIds.stream().map(smeId -> new DefaultProperty.Builder().value(smeId).build()).collect(Collectors.toList());

		SubmodelElementList submodelIdList = new DefaultSubmodelElementList.Builder().idShort("submodelIds").value(submodelIdProperties).build();
		SubmodelElementList smeIdList = new DefaultSubmodelElementList.Builder().idShort("submodelElementIdShortPaths").value(smeIdProperties).build();
		SubmodelElement typeProperty = createTypeProperty(type);

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").value(Arrays.asList(submodelIdList, smeIdList, typeProperty)).build();

		TargetInformation result = submodelTargetInformationAdapter.adapt(targetInformationSMC);

		assertTrue(result instanceof SubmodelTargetInformation);
		assertEquals(expectedSubmodelIds, ((SubmodelTargetInformation) result).getSubmodelIds());
		assertEquals(expectedSmeIds, ((SubmodelTargetInformation) result).getSubmodelElementIdShortPaths());
	}

	@Test
	public void testAdaptTargetInformationWithEmptySubmodelIds() {

		List<String> submodelIds = Collections.emptyList();
		List<String> smeIds = Collections.emptyList();

		TargetInformation targetInformation = new SubmodelTargetInformation(submodelIds, smeIds);

		SubmodelElementCollection result = submodelTargetInformationAdapter.adapt(targetInformation);

		assertEquals("targetInformation", result.getIdShort());

		List<SubmodelElement> elements = result.getValue();
		assertEquals(3, elements.size());

		SubmodelElementList submodelIdList = (SubmodelElementList) elements.get(0);
		assertEquals("submodelIds", submodelIdList.getIdShort());

		SubmodelElementList smeIdList = (SubmodelElementList) elements.get(1);
		assertEquals("submodelElementIdShortPaths", smeIdList.getIdShort());

		Property typeProperty = (Property) elements.get(2);
		assertEquals("@type", typeProperty.getIdShort());

		List<String> actualSubmodelIds = submodelIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
		assertTrue(actualSubmodelIds.isEmpty());

		List<String> actualSmeIds = smeIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
		assertTrue(actualSmeIds.isEmpty());

		String actualType = typeProperty.getValue();
		assertTrue(actualType.equals("submodel"));
	}

	@Test(expected = InvalidTargetInformationException.class)
	public void testAdaptSubmodelElementCollectionWithInvalidStructure() {

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation")
				.value(Collections.singletonList(new DefaultProperty.Builder().idShort("invalidElement").value("value").build())).build();

		submodelTargetInformationAdapter.adapt(targetInformationSMC);
	}

	@Test(expected = InvalidTargetInformationException.class)
	public void testAdaptSubmodelElementCollectionWithoutSubmodelIds() {

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").value(Collections.emptyList()).build();

		submodelTargetInformationAdapter.adapt(targetInformationSMC);
	}

	private SubmodelElement createTypeProperty(String type) {
		return new DefaultProperty.Builder().idShort("@type").value(type).build();
	}

}
