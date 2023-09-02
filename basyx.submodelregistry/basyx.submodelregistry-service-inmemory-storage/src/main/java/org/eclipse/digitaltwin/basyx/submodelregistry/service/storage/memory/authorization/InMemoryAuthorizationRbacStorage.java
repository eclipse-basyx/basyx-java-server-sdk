package org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.memory.authorization;

import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;

@RequiredArgsConstructor
public class InMemoryAuthorizationRbacStorage implements IRbacStorage {
    private final RbacRuleSet rbacRuleSet;

    @Override
    public RbacRuleSet getRbacRuleSet() {
        return rbacRuleSet;
    }

    @Override
    public void addRule(RbacRule rbacRule) {
        rbacRuleSet.addRule(rbacRule);
    }

    @Override
    public void removeRule(RbacRule rbacRule) {
        rbacRuleSet.deleteRule(rbacRule);
    }
}
