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

package org.eclipse.digitaltwin.basyx.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleKeyGenerator;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Test suite for {@link RbacStorage}
 * 
 * @author danish
 */
public abstract class RbacStorageTestSuite {
	
	@Mock
    protected TargetInformation targetInformation;

    protected RbacStorage rbacStorage;
    
    protected RbacRule expectedRule;
    
    protected abstract void setUpRbacStorage();

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        setUpRbacStorage();
    }

    @Test
    public void testAddRule() {
    	
    	expectedRule = createRbacRule("Engineer", Arrays.asList(Action.READ), targetInformation);
        
        String key = RbacRuleKeyGenerator.generateKey(expectedRule.getRole(), expectedRule.getAction().get(0).toString(), expectedRule.getTargetInformation().getClass().getName());
        
        rbacStorage.addRule(expectedRule);

        assertTrue(rbacStorage.exist(key));
    }

    @Test
    public void testGetRbacRule() {
    	
    	expectedRule = createRbacRule("Maintainer", Arrays.asList(Action.CREATE), targetInformation);
        
        String key = RbacRuleKeyGenerator.generateKey(expectedRule.getRole(), expectedRule.getAction().get(0).toString(), expectedRule.getTargetInformation().getClass().getName());

        rbacStorage.addRule(expectedRule);

        RbacRule retrievedRule = rbacStorage.getRbacRule(key);

        assertNotNull(retrievedRule);
        assertEquals(expectedRule, retrievedRule);
    }

    @Test
    public void testRemoveRule() {
    	
    	expectedRule = createRbacRule("Supplier", Arrays.asList(Action.EXECUTE), targetInformation);
        
        String key = RbacRuleKeyGenerator.generateKey(expectedRule.getRole(), expectedRule.getAction().get(0).toString(), expectedRule.getTargetInformation().getClass().getName());

        rbacStorage.addRule(expectedRule);

        rbacStorage.removeRule(key);

        assertFalse(rbacStorage.exist(key));
    }

    @Test
    public void testExistWhenElementExists() {
    	
    	expectedRule = createRbacRule("Developer", Arrays.asList(Action.UPDATE), targetInformation);
    	
        String key = RbacRuleKeyGenerator.generateKey(expectedRule.getRole(), expectedRule.getAction().get(0).toString(), expectedRule.getTargetInformation().getClass().getName());

        rbacStorage.addRule(expectedRule);

        boolean exists = rbacStorage.exist(key);

        assertTrue(exists);
    }

    @Test
    public void testExistWhenElementDoesNotExist() {
    	
        boolean exists = rbacStorage.exist("nonexistentKey");
        
        assertFalse(exists);
    }
    
    @Test
    public void testGetRbacRules() {
    	
    	RbacRule rule1 = createRbacRule("Role_1", Collections.singletonList(Action.CREATE), targetInformation);
    	RbacRule rule2 = createRbacRule("Role_2", Collections.singletonList(Action.READ), targetInformation);

    	rbacStorage.addRule(rule1);
    	rbacStorage.addRule(rule2);

        Map<String, RbacRule> result = rbacStorage.getRbacRules();

        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    public RbacRule createRbacRule(String role, List<Action> actions, TargetInformation targetInformation) {
    	return new RbacRule(role, actions, targetInformation);
    }

}
