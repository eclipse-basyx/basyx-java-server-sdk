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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization;

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
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.rbac.backend.submodel.CDTargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidTargetInformationException;

/**
 * Tests {@link CDTargetInformationAdapter}
 * 
 * @author danish
 */
public class CDTargetInformationAdapterTest {

	private CDTargetInformationAdapter cdTargetInformationAdapter;

	@Before
	public void setUp() {
		cdTargetInformationAdapter = new CDTargetInformationAdapter();
	}

	@Test
	public void testAdaptTargetInformationToSubmodelElementCollection() {

		List<String> conceptDescriptiornIds = Arrays.asList("cdId1", "cdId2");
		TargetInformation targetInformation = new ConceptDescriptionTargetInformation(conceptDescriptiornIds);

		SubmodelElementCollection result = cdTargetInformationAdapter.adapt(targetInformation);

		assertEquals("targetInformation", result.getIdShort());

		List<SubmodelElement> elements = result.getValue();
		assertEquals(2, elements.size());

		SubmodelElementList cdIdList = (SubmodelElementList) elements.get(0);
		assertEquals("conceptDescriptionIds", cdIdList.getIdShort());
		
		Property typeProperty = (Property) elements.get(1);
		assertEquals("@type", typeProperty.getIdShort());

		List<String> actualCDIds = cdIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
		assertEquals(conceptDescriptiornIds, actualCDIds);
		
		String actualType = typeProperty.getValue();
        assertTrue(actualType.equals("concept-description")); 
	}

	@Test
	public void testAdaptSubmodelElementCollectionToTargetInformation() {

		List<String> expectedCDIds = Arrays.asList("cdId1", "cdId2");
		String type = "concept-description";
		
		List<SubmodelElement> cdIdProperties = expectedCDIds.stream().map(aasId -> new DefaultProperty.Builder().value(aasId).build()).collect(Collectors.toList());

		SubmodelElementList cdIdList = new DefaultSubmodelElementList.Builder().idShort("conceptDescriptionIds").value(cdIdProperties).build();
		SubmodelElement typeProperty = createTypeProperty(type);
		
		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").value(Arrays.asList(cdIdList, typeProperty)).build();

		TargetInformation result = cdTargetInformationAdapter.adapt(targetInformationSMC);

		assertTrue(result instanceof ConceptDescriptionTargetInformation);
		assertEquals(expectedCDIds, ((ConceptDescriptionTargetInformation) result).getConceptDescriptionIds());
	}
	
    @Test
    public void testAdaptTargetInformationWithEmptyCDIds() {
    	
        List<String> cdIds = Collections.emptyList();
        TargetInformation targetInformation = new ConceptDescriptionTargetInformation(cdIds);

        SubmodelElementCollection result = cdTargetInformationAdapter.adapt(targetInformation);

        assertEquals("targetInformation", result.getIdShort());

        List<SubmodelElement> elements = result.getValue();
        assertEquals(2, elements.size());

        SubmodelElementList cdIdList = (SubmodelElementList) elements.get(0);
        assertEquals("conceptDescriptionIds", cdIdList.getIdShort());
        
        Property typeProperty = (Property) elements.get(1);
		assertEquals("@type", typeProperty.getIdShort());

        List<String> actualCDIds = cdIdList.getValue().stream()
                .map(Property.class::cast)
                .map(Property::getValue)
                .map(String::valueOf)
                .collect(Collectors.toList());
        assertTrue(actualCDIds.isEmpty());
        
        String actualType = typeProperty.getValue();
        assertTrue(actualType.equals("concept-description")); 
    }
    
    @Test(expected = InvalidTargetInformationException.class)
    public void testAdaptSubmodelElementCollectionWithInvalidStructure() {
    	
        SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation")
                .value(Collections.singletonList(new DefaultProperty.Builder().idShort("invalidElement").value("value").build()))
                .build();

        cdTargetInformationAdapter.adapt(targetInformationSMC);
    }
    
    @Test(expected = InvalidTargetInformationException.class)
    public void testAdaptSubmodelElementCollectionWithoutCDIds() {
    	
        SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation")
                .value(Collections.emptyList())
                .build();

        cdTargetInformationAdapter.adapt(targetInformationSMC);
    }
    
    private SubmodelElement createTypeProperty(String type) {
		return new DefaultProperty.Builder().idShort("@type").value(type).build();
	}

}