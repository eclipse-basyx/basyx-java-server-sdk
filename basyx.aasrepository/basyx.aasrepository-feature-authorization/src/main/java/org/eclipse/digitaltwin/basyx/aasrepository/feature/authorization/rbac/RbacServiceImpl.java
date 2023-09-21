package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.rbac;


import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnExpression(value = "'${basyx.aasrepository.feature.authorization.enabled:false}' and '${basyx.aasrepository.feature.authorization.type}' == 'rbac'")
public class RbacServiceImpl<RbacRuleFilterType> implements IRbacService {
    private final RbacPermissionResolver<RbacRuleFilterType> rbacPermissionResolver;
    private final IRbacStorage<RbacRuleFilterType> storage;

    public RbacServiceImpl(RbacPermissionResolver<RbacRuleFilterType> rbacPermissionResolver, IRbacStorage<RbacRuleFilterType> storage) {
        this.rbacPermissionResolver = rbacPermissionResolver;
        this.storage = storage;
    }

    @Override
    public RbacRuleSet getRbacRuleSet() {
        return storage.getRbacRuleSet(rbacPermissionResolver.getGetRbacRuleSetFilterInfo());
    }

    @Override
    public void addRule(RbacRule rbacRule) {
        rbacPermissionResolver.addRule(rbacRule);
        storage.addRule(rbacRule);
    }

    @Override
    public void removeRule(RbacRule rbacRule) {
        rbacPermissionResolver.addRule(rbacRule);
        storage.removeRule(rbacRule);
    }
}
