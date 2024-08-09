package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.inmemory;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.authorization.RbacStorageTestSuite;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;

public class InMemoryRbacStorageTest extends RbacStorageTestSuite {

    @Override
    protected void setUpRbacStorage() {
        Map<String, RbacRule> initialRules = new HashMap<>();
        rbacStorage = new InMemoryAuthorizationRbacStorage(initialRules);
    }
    
}
