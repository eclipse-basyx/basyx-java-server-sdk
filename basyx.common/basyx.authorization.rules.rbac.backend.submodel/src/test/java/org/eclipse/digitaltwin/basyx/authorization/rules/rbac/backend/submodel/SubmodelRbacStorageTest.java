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

import org.mockito.Mock;
import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.authorization.RbacStorageTestSuite;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleKeyGenerator;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.core.pagination.CursorResult;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Tests {@link SubmodelAuthorizationRbacStorage}
 * 
 * @author danish
 */
public class SubmodelRbacStorageTest extends RbacStorageTestSuite {

	private static final String ROLE_DUMMY_DEVELOPER = "Dummy_Developer";

	private static final String ROLE_DUMMY_ENGINEER = "Dummy_Engineer";

	private static final List<Action> DELETE_ACTIONS = Arrays.asList(Action.CREATE);

	private static final String DELETE_ROLE = "Auditer";

	@Mock
	private ConnectedSubmodelService smService;

	@Mock
	private RbacRuleAdapter ruleAdapter;

	@Before
	@Override
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		setUpRbacStorage();
	}

	@Override
	protected void setUpRbacStorage() {
		rbacStorage = new SubmodelAuthorizationRbacStorage(smService, new HashMap<>(), ruleAdapter);

		RbacRule rule = RbacRuleAdapterTest.createDummyRbacRule("Admin", DELETE_ACTIONS, mock(TargetInformation.class));

		SubmodelElementCollection smc = RbacRuleAdapterTest.createDummySMC(rule, createDummyKey(rule.getRole(), rule.getAction(), rule.getTargetInformation().getClass().getName()));

		createMockExpectations(smc);
	}

	@Test
	@Override
	public void testRemoveRule() {

		RbacRule expectedRule = createRbacRule(DELETE_ROLE, DELETE_ACTIONS, targetInformation);

		String key = createDummyKey(DELETE_ROLE, DELETE_ACTIONS, targetInformation.getClass().getName());

		rbacStorage.addRule(expectedRule);

		rbacStorage.removeRule(key);

		assertFalse(rbacStorage.exist(key));
	}

	private String createDummyKey(String role, List<Action> actions, String clazz) {
		return RbacRuleKeyGenerator.generateKey(role, actions.get(0).toString(), clazz);
	}

	private void createMockExpectations(SubmodelElementCollection smc) {
		when(ruleAdapter.adapt(any(RbacRule.class), anyString())).thenReturn(smc);
        
        when(smService.getSubmodelElement(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0, String.class);
            if ("nonexistentKey".equals(key)) {
                throw new ElementDoesNotExistException();
            } 
            
            return smc;
        });

        when(smService.getSubmodelElement(createDummyKey(DELETE_ROLE, DELETE_ACTIONS, targetInformation.getClass().getName()))).thenThrow(new ElementDoesNotExistException());
      
	    when(ruleAdapter.adapt(any(SubmodelElementCollection.class))).thenAnswer(invocation -> {
          SubmodelElementCollection smcRule = invocation.getArgument(0, SubmodelElementCollection.class);
          
          String role = smcRule.getValue().stream().filter(sme -> sme.getIdShort().equals("role")).map(Property.class::cast).map(Property::getValue).findAny().get();
          
          if (role.equals(ROLE_DUMMY_ENGINEER) || role.equals(ROLE_DUMMY_DEVELOPER))
        	  return RbacRuleAdapterTest.createDummyRbacRule(role, DELETE_ACTIONS,  mock(TargetInformation.class));
          
          return expectedRule;
	    });
        
        @SuppressWarnings("unchecked")
		CursorResult<List<SubmodelElement>> cursorResult = mock(CursorResult.class);
        
        when(smService.getSubmodelElements(any(PaginationInfo.class))).thenReturn(cursorResult);
        
        when(cursorResult.getResult()).thenAnswer(new Answer<List<SubmodelElement>>() {
        	
            @Override
            public List<SubmodelElement> answer(InvocationOnMock invocation) throws Throwable {
            	RbacRule rbacRule1 = RbacRuleAdapterTest.createDummyRbacRule(ROLE_DUMMY_ENGINEER, Arrays.asList(Action.READ), mock(TargetInformation.class));
            	RbacRule rbacRule2 = RbacRuleAdapterTest.createDummyRbacRule(ROLE_DUMMY_DEVELOPER, Arrays.asList(Action.READ), mock(TargetInformation.class));
            	
            	SubmodelElement rule1 = RbacRuleAdapterTest.createDummySMC(rbacRule1, createDummyKey(rbacRule1.getRole(), rbacRule1.getAction(), rbacRule1.getTargetInformation().getClass().getName()));
            	SubmodelElement rule2 = RbacRuleAdapterTest.createDummySMC(rbacRule2, createDummyKey(rbacRule2.getRole(), rbacRule2.getAction(), rbacRule2.getTargetInformation().getClass().getName()));
                return Arrays.asList(rule1, rule2);
            }
            
        });
	}

}
