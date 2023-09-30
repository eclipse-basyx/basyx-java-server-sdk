package org.eclipse.digitaltwin.basyx.authorization.rbac;


import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY)
@ConditionalOnExpression(value = "'${" + CommonAuthorizationConfig.TYPE_PROPERTY_KEY + "}' == 'rbac'")
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
