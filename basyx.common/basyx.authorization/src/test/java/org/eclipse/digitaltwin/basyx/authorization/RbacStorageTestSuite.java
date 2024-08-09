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
import java.util.Map;

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
        // Arrange
        String key = "someKey";
        when(rbacRule.getAction()).thenReturn(Arrays.asList(Action.READ));
        when(rbacRule.getRole()).thenReturn("ROLE_1");
        when(rbacRule.getTargetInformation()).thenReturn(targetInformation);
        
        // Act
        rbacStorage.addRule(rbacRule);

        // Assert
        assertTrue(rbacStorage.exist(RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName())));
    }

    @Test
    public void testGetRbacRule() {
        // Arrange
        String key = "someKey";
        when(rbacRule.getAction()).thenReturn(Arrays.asList(Action.READ));
        when(rbacRule.getRole()).thenReturn("ROLE_1");
        when(rbacRule.getTargetInformation()).thenReturn(targetInformation);

        rbacStorage.addRule(rbacRule);

        // Act
        RbacRule retrievedRule = rbacStorage.getRbacRule(key);

        // Assert
        assertNotNull(retrievedRule);
        assertEquals(rbacRule, retrievedRule);
    }

    @Test
    public void testRemoveRule() {
        // Arrange
        String key = "someKey";
        when(rbacRule.getAction()).thenReturn(Arrays.asList(Action.READ));
        when(rbacRule.getRole()).thenReturn("ROLE_1");
        when(rbacRule.getTargetInformation()).thenReturn(targetInformation);

        rbacStorage.addRule(rbacRule);

        // Act
        rbacStorage.removeRule(key);

        // Assert
        assertFalse(rbacStorage.exist(key));
    }

    @Test
    public void testExistWhenElementExists() {
        // Arrange
        String key = "someKey";
        when(rbacRule.getAction()).thenReturn(Arrays.asList(Action.READ));
        when(rbacRule.getRole()).thenReturn("ROLE_1");
        when(rbacRule.getTargetInformation()).thenReturn(targetInformation);

        rbacStorage.addRule(rbacRule);

        // Act
        boolean exists = rbacStorage.exist(key);

        // Assert
        assertTrue(exists);
    }

    @Test
    public void testExistWhenElementDoesNotExist() {
        // Act
        boolean exists = rbacStorage.exist("nonexistentKey");

        // Assert
        assertFalse(exists);
    }

}
