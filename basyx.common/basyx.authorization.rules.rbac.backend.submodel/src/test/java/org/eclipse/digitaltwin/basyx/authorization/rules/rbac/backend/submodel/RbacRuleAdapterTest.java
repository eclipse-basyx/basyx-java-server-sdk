/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleKeyGenerator;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tests {@link RbacRuleAdapterTest}
 * 
 * @author danish
 */
public class RbacRuleAdapterTest {

    @Mock
    private TargetInformationAdapter targetInformationAdapter;
    
    @Mock
    private static TargetInformation targetInformation;

    private RbacRuleAdapter rbacRuleAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rbacRuleAdapter = new RbacRuleAdapter(targetInformationAdapter);
    }
    
    @Test
    public void adaptRbacRuleToSubmodelElementCollection() {

        String role = "admin";
        List<Action> actions = Arrays.asList(Action.READ);
        
        String rbacRuleKey = RbacRuleKeyGenerator.generateKey(role, actions.get(0).toString(), targetInformation.getClass().getName());

        RbacRule rbacRule = new RbacRule(role, actions, targetInformation);
        
        SubmodelElementCollection expectedSMCRule = createDummySMC(rbacRule, rbacRuleKey);
        
        SubmodelElementCollection expectedTargetInformation = createDummyTargetInformation();
        
        when(targetInformationAdapter.adapt(targetInformation)).thenReturn(expectedTargetInformation);

        SubmodelElementCollection actualSMCRule = rbacRuleAdapter.adapt(rbacRule, rbacRuleKey);

        assertEquals(rbacRuleKey, actualSMCRule.getIdShort());

        List<SubmodelElement> elements = actualSMCRule.getValue();
        assertEquals(3, elements.size());

        Property roleProperty = (Property) elements.stream().filter(sme -> sme.getIdShort().equals("role")).findAny().get();
        assertEquals(role, roleProperty.getValue());

        SubmodelElementList actionList = (SubmodelElementList) elements.stream().filter(sme -> sme.getIdShort().equals("action")).findAny().get();
        List<String> actionValues = actionList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
        assertEquals(Arrays.asList("READ"), actionValues);

        SubmodelElementCollection actualTargetInformation = (SubmodelElementCollection) elements.stream().filter(sme -> sme.getIdShort().equals("targetInformation")).findAny().get();
        assertSame(expectedTargetInformation, actualTargetInformation);
        
        assertEquals(expectedSMCRule, actualSMCRule);
    }
    
    @Test
    public void adaptSubmodelElementCollectionToRbacRule() {
        
    	RbacRule expectedRbacRule = createDummyRbacRule("admin", Arrays.asList(Action.READ), targetInformation);
    	
    	String rbacRuleKey = RbacRuleKeyGenerator.generateKey(expectedRbacRule.getRole(), expectedRbacRule.getAction().get(0).toString(), expectedRbacRule.getTargetInformation().getClass().getName());

        when(targetInformationAdapter.adapt(createDummyTargetInformation())).thenReturn(targetInformation);

        SubmodelElementCollection rbacRuleSMC = createDummySMC(expectedRbacRule, rbacRuleKey);

        RbacRule actualRbacRule = rbacRuleAdapter.adapt(rbacRuleSMC);

        assertEquals(expectedRbacRule, actualRbacRule);
    }
    
    @Test
    public void targetInformationAdapterInvoked() {
        String rbacRuleKey = "testRuleKey";
        String role = "admin";
        List<Action> actions = Arrays.asList(Action.READ);

        RbacRule rbacRule = new RbacRule(role, actions, targetInformation);

        SubmodelElementCollection expectedTargetInformation = createDummyTargetInformation();
        
        when(targetInformationAdapter.adapt(targetInformation)).thenReturn(expectedTargetInformation);

        rbacRuleAdapter.adapt(rbacRule, rbacRuleKey);

        verify(targetInformationAdapter, times(1)).adapt(targetInformation);
    }

    
    public static SubmodelElementCollection createDummySMC(RbacRule rbacRule, String rbacRuleKey) {
    	
    	SubmodelElementCollection rule = new DefaultSubmodelElementCollection.Builder().idShort(rbacRuleKey).build();
		
		Property role = new DefaultProperty.Builder().idShort("role").value(rbacRule.getRole()).build();
		SubmodelElementList action = new DefaultSubmodelElementList.Builder().idShort("action").build();
		
		List<SubmodelElement> actions = rbacRule.getAction().stream().map(RbacRuleAdapterTest::transform).collect(Collectors.toList());
		action.setValue(actions);
		
		SubmodelElementCollection targetInformation = createDummyTargetInformation();
		
		rule.setValue(Arrays.asList(role, action, targetInformation));
		
		return rule;
    }
    
    public static RbacRule createDummyRbacRule(String role, List<Action> actions, TargetInformation targetInformation) {
		
		return new RbacRule(role, actions, targetInformation);
    }

	private static SubmodelElementCollection createDummyTargetInformation() {
		SubmodelElementCollection targetInformation = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").build();
		
		SubmodelElementList targetInformationSML = new DefaultSubmodelElementList.Builder().idShort("aasIds").build();
		
		Property targetInformationProperty = new DefaultProperty.Builder().value("dummyAasId").build();
		
		targetInformationSML.setValue(Arrays.asList(targetInformationProperty));
		
		targetInformation.setValue(Arrays.asList(targetInformationSML));
		
		return targetInformation;
	}
    
    private static Property transform(Action action) {
		return new DefaultProperty.Builder().value(action.toString()).build();
	}

}
