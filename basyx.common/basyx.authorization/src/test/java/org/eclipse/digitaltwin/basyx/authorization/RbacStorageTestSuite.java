package org.eclipse.digitaltwin.basyx.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

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
import java.util.List;

public abstract class RbacStorageTestSuite {
	
	@Mock
    protected RbacRule rbacRule;
	
	@Mock
    protected TargetInformation targetInformation;

    protected RbacStorage rbacStorage;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        
        setUpRbacStorage();
    }

    protected abstract void setUpRbacStorage();

    @Test
    public void testAddRule() {
    	
    	configureMockRbacRule("Engineer", Arrays.asList(Action.READ), targetInformation);
        
        String key = RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName());
        
        rbacStorage.addRule(rbacRule);

        assertTrue(rbacStorage.exist(key));
    }

    @Test
    public void testGetRbacRule() {
    	
    	configureMockRbacRule("Engineer", Arrays.asList(Action.READ), targetInformation);
        
        String key = RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName());

        rbacStorage.addRule(rbacRule);

        RbacRule retrievedRule = rbacStorage.getRbacRule(key);

        assertNotNull(retrievedRule);
        assertEquals(rbacRule, retrievedRule);
    }

    @Test
    public void testRemoveRule() {
    	
    	configureMockRbacRule("Engineer", Arrays.asList(Action.READ), targetInformation);
        
        String key = RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName());

        rbacStorage.addRule(rbacRule);

        rbacStorage.removeRule(key);

        assertFalse(rbacStorage.exist(key));
    }

    @Test
    public void testExistWhenElementExists() {
    	
    	configureMockRbacRule("Engineer", Arrays.asList(Action.READ), targetInformation);
    	
        String key = RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName());

        rbacStorage.addRule(rbacRule);

        boolean exists = rbacStorage.exist(key);

        assertTrue(exists);
    }

    @Test
    public void testExistWhenElementDoesNotExist() {
    	
        boolean exists = rbacStorage.exist("nonexistentKey");
        
        assertFalse(exists);
    }
    
    public void configureMockRbacRule(String role, List<Action> actions, TargetInformation targetInformation) {
    	when(rbacRule.getRole()).thenReturn(role);
		when(rbacRule.getAction()).thenReturn(actions);
        when(rbacRule.getTargetInformation()).thenReturn(targetInformation);
	}

}
