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

import org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization.rbac.backend.submodel.AasEnvironmentTargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.core.exceptions.InvalidTargetInformationException;

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
		TargetInformation targetInformation = new AasEnvironmentTargetInformation(aasIds, submodelIds);

		SubmodelElementCollection result = aasEnvironmentTargetInformationAdapter.adapt(targetInformation);

		assertEquals("targetInformation", result.getIdShort());

		List<SubmodelElement> elements = result.getValue();
		assertEquals(3, elements.size());

		SubmodelElementList aasIdList = (SubmodelElementList) elements.get(0);
		assertEquals("aasIds", aasIdList.getIdShort());
		
		SubmodelElementList submodelIdList = (SubmodelElementList) elements.get(1);
		assertEquals("submodelIds", submodelIdList.getIdShort());
		
		Property typeProperty = (Property) elements.get(2);
		assertEquals("@type", typeProperty.getIdShort());

		List<String> actualAasIds = aasIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
		assertEquals(aasIds, actualAasIds);
		
		List<String> actualSubmodelIds = submodelIdList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
		assertEquals(submodelIds, actualSubmodelIds);
		
		String actualType = typeProperty.getValue();
        assertTrue(actualType.equals("aas-environment")); 
	}

	@Test
	public void testAdaptSubmodelElementCollectionToTargetInformation() {

		List<String> expectedAasIds = Arrays.asList("aasId1", "aasId2");
		List<String> expectedSubmodelIds = Arrays.asList("smId1", "smId2");
		String type = "aas-environment";
		
		List<SubmodelElement> aasIdProperties = expectedAasIds.stream().map(aasId -> new DefaultProperty.Builder().value(aasId).build()).collect(Collectors.toList());
		List<SubmodelElement> submodelIdProperties = expectedSubmodelIds.stream().map(submodelId -> new DefaultProperty.Builder().value(submodelId).build()).collect(Collectors.toList());

		SubmodelElementList aasIdList = new DefaultSubmodelElementList.Builder().idShort("aasIds").value(aasIdProperties).build();
		SubmodelElementList submodelIdList = new DefaultSubmodelElementList.Builder().idShort("submodelIds").value(submodelIdProperties).build();
		SubmodelElement typeProperty = createTypeProperty(type);

		SubmodelElementCollection targetInformationSMC = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").value(Arrays.asList(aasIdList, submodelIdList, typeProperty)).build();
		
		TargetInformation result = aasEnvironmentTargetInformationAdapter.adapt(targetInformationSMC);

		assertTrue(result instanceof AasEnvironmentTargetInformation);
		assertEquals(expectedAasIds, ((AasEnvironmentTargetInformation) result).getAasIds());
		assertEquals(expectedSubmodelIds, ((AasEnvironmentTargetInformation) result).getSubmodelIds());
	}

	@Test
    public void testAdaptTargetInformationWithEmptyAasIds() {
    	
        List<String> aasIds = Collections.emptyList();
        List<String> submodelIds = Collections.emptyList();
        
        TargetInformation targetInformation = new AasEnvironmentTargetInformation(aasIds, submodelIds);

        SubmodelElementCollection result = aasEnvironmentTargetInformationAdapter.adapt(targetInformation);

        assertEquals("targetInformation", result.getIdShort());

        List<SubmodelElement> elements = result.getValue();
        assertEquals(3, elements.size());

        SubmodelElementList aasIdList = (SubmodelElementList) elements.get(0);
        assertEquals("aasIds", aasIdList.getIdShort());
        
        SubmodelElementList submodelIdList = (SubmodelElementList) elements.get(1);
        assertEquals("submodelIds", submodelIdList.getIdShort());
        
        Property typeProperty = (Property) elements.get(2);
        assertEquals("@type", typeProperty.getIdShort());

        List<String> actualAasIds = aasIdList.getValue().stream()
                .map(Property.class::cast)
                .map(Property::getValue)
                .map(String::valueOf)
                .collect(Collectors.toList());
        assertTrue(actualAasIds.isEmpty());
        
        List<String> actualSubmodelIds = submodelIdList.getValue().stream()
        		.map(Property.class::cast)
        		.map(Property::getValue)
        		.map(String::valueOf)
        		.collect(Collectors.toList());
        assertTrue(actualSubmodelIds.isEmpty());
        
        String actualType = typeProperty.getValue();
        assertTrue(actualType.equals("aas-environment"));
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
    
    private SubmodelElement createTypeProperty(String type) {
		return new DefaultProperty.Builder().idShort("@type").value(type).build();
	}

}