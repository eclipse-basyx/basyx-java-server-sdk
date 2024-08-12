package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel;

import org.mockito.Mock;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.authorization.RbacStorageTestSuite;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleKeyGenerator;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SubmodelRbacStorageTest extends RbacStorageTestSuite {

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
        
        configureMockRbacRule("Engineer", Arrays.asList(Action.READ), targetInformation);
        
        String key = RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName());
        
        SubmodelElementCollection smc = mock(SubmodelElementCollection.class);
        when(ruleAdapter.adapt(rbacRule, key)).thenReturn(smc);
        when(smService.getSubmodelElement(key)).thenReturn(smc);
        doNothing().when(smService).createSubmodelElement(smc);
        doNothing().when(smService).deleteSubmodelElement(key);
        when(smService.getSubmodelElement("nonexistentKey")).thenThrow(new ElementDoesNotExistException());
        when(smService.getSubmodelElement(getDeletionKey())).thenThrow(new ElementDoesNotExistException());
        when(ruleAdapter.adapt(smc)).thenReturn(rbacRule);
    }
    
    @Test
    @Override
    public void testRemoveRule() {
    	
    	configureMockRbacRule(DELETE_ROLE, DELETE_ACTIONS, targetInformation);
        
        String key = getDeletionKey();

        rbacStorage.addRule(rbacRule);

        rbacStorage.removeRule(key);

        assertFalse(rbacStorage.exist(key));
    }
    
    private String getDeletionKey() {
    	return RbacRuleKeyGenerator.generateKey(DELETE_ROLE, DELETE_ACTIONS.get(0).toString(), rbacRule.getTargetInformation().getClass().getName());
    }
    
}
