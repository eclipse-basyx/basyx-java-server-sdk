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

package org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization.rbac.backend.submodel.AasEnvironmentTargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidTargetInformationException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link AasEnvironmentTargetInformationAdapter}
 * 
 * @author danish
 */
public class AasEnvironmentTargetInformationAdapterTest {

	private AasEnvironmentTargetInformationAdapter aasEnvironmentTargetInformationAdapter;

	@Before
	public void setUp() {
		aasEnvironmentTargetInformationAdapter = new AasEnvironmentTargetInformationAdapter();
	}

	@Test
	public void testAdaptTargetInformationToSubmodelElementCollection() {
		List<String> aasIds = Arrays.asList("aasId1", "aasId2");
		List<String> submodelIds = Arrays.asList("smId1", "smId2");
		List<String> conceptDescriptionIds = Arrays.asList("cdId1", "cdId2");
		TargetInformation targetInformation = new AasEnvironmentTargetInformation(aasIds, submodelIds, conceptDescriptionIds);

		SubmodelElementCollection result = aasEnvironmentTargetInformationAdapter.adapt(targetInformation);

		assertEquals("targetInformation", result.getIdShort());

		List<SubmodelElement> elements = result.getValue();
		assertEquals(4, elements.size());
		assertElementList(elements.get(0), "aasIds", aasIds);
		assertElementList(elements.get(1), "submodelIds", submodelIds);
		assertElementList(elements.get(2), "conceptDescriptionIds", conceptDescriptionIds);
		assertType(elements.get(3));
	}

	@Test
	public void testAdaptSubmodelElementCollectionToTargetInformation() {
		List<String> expectedAasIds = Arrays.asList("aasId1", "aasId2");
		List<String> expectedSubmodelIds = Arrays.asList("smId1", "smId2");
		List<String> expectedConceptDescriptionIds = Arrays.asList("cdId1", "cdId2");

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation")
				.value(Arrays.asList(createElementList("aasIds", expectedAasIds), createElementList("submodelIds", expectedSubmodelIds), createElementList("conceptDescriptionIds", expectedConceptDescriptionIds), createTypeProperty()))
				.build();

		TargetInformation result = aasEnvironmentTargetInformationAdapter.adapt(targetInformationSMC);

		assertTargetInformation(result, expectedAasIds, expectedSubmodelIds, expectedConceptDescriptionIds);
	}

	@Test
	public void testAdaptLegacySubmodelElementCollectionToTargetInformationDefaultsConceptDescriptionIds() {
		List<String> expectedAasIds = Arrays.asList("aasId1", "aasId2");
		List<String> expectedSubmodelIds = Arrays.asList("smId1", "smId2");

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation")
				.value(Arrays.asList(createElementList("aasIds", expectedAasIds), createElementList("submodelIds", expectedSubmodelIds), createTypeProperty()))
				.build();

		TargetInformation result = aasEnvironmentTargetInformationAdapter.adapt(targetInformationSMC);

		assertTargetInformation(result, expectedAasIds, expectedSubmodelIds, Collections.singletonList("*"));
	}

	@Test
	public void testAdaptTargetInformationWithEmptyIds() {
		List<String> aasIds = Collections.emptyList();
		List<String> submodelIds = Collections.emptyList();
		List<String> conceptDescriptionIds = Collections.emptyList();
		TargetInformation targetInformation = new AasEnvironmentTargetInformation(aasIds, submodelIds, conceptDescriptionIds);

		SubmodelElementCollection result = aasEnvironmentTargetInformationAdapter.adapt(targetInformation);

		assertEquals("targetInformation", result.getIdShort());

		List<SubmodelElement> elements = result.getValue();
		assertEquals(4, elements.size());
		assertElementList(elements.get(0), "aasIds", aasIds);
		assertElementList(elements.get(1), "submodelIds", submodelIds);
		assertElementList(elements.get(2), "conceptDescriptionIds", conceptDescriptionIds);
		assertType(elements.get(3));
	}

	@Test(expected = InvalidTargetInformationException.class)
	public void testAdaptSubmodelElementCollectionWithInvalidStructure() {
		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation")
				.value(Collections.singletonList(new DefaultProperty.Builder().idShort("invalidElement").value("value").build()))
				.build();

		aasEnvironmentTargetInformationAdapter.adapt(targetInformationSMC);
	}

	@Test(expected = InvalidTargetInformationException.class)
	public void testAdaptSubmodelElementCollectionWithoutAasIds() {
		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation")
				.value(Collections.emptyList())
				.build();

		aasEnvironmentTargetInformationAdapter.adapt(targetInformationSMC);
	}

	private void assertElementList(SubmodelElement element, String idShort, List<String> expectedValues) {
		SubmodelElementList elementList = (SubmodelElementList) element;

		assertEquals(idShort, elementList.getIdShort());
		assertEquals(expectedValues, getValues(elementList));
	}

	private void assertType(SubmodelElement element) {
		Property typeProperty = (Property) element;

		assertEquals("@type", typeProperty.getIdShort());
		assertEquals("aas-environment", typeProperty.getValue());
	}

	private void assertTargetInformation(TargetInformation result, List<String> expectedAasIds, List<String> expectedSubmodelIds, List<String> expectedConceptDescriptionIds) {
		assertTrue(result instanceof AasEnvironmentTargetInformation);
		AasEnvironmentTargetInformation aasEnvironmentTargetInformation = (AasEnvironmentTargetInformation) result;

		assertEquals(expectedAasIds, aasEnvironmentTargetInformation.getAasIds());
		assertEquals(expectedSubmodelIds, aasEnvironmentTargetInformation.getSubmodelIds());
		assertEquals(expectedConceptDescriptionIds, aasEnvironmentTargetInformation.getConceptDescriptionIds());
	}

	private SubmodelElementList createElementList(String idShort, List<String> values) {
		List<SubmodelElement> properties = values.stream().map(value -> new DefaultProperty.Builder().value(value).build()).collect(Collectors.toList());

		return new DefaultSubmodelElementList.Builder().idShort(idShort).value(properties).build();
	}

	private List<String> getValues(SubmodelElementList elementList) {
		return elementList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
	}

	private SubmodelElement createTypeProperty() {
		return new DefaultProperty.Builder().idShort("@type").value("aas-environment").build();
	}

}
