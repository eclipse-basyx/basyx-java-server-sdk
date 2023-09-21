package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.stream.Collectors;

@ConditionalOnExpression(value = "'${basyx.submodelregistry.feature.authorization.type}' == 'rbac' and '${registry.type}'.equals('inMemory')")
@Service
public class InMemoryAuthorizationRbacStorage implements IRbacStorage<Predicate<RbacRule>> {
    private final RbacRuleSet rbacRuleSet;

    public InMemoryAuthorizationRbacStorage(RbacRuleSet rbacRuleSet) {
        this.rbacRuleSet = rbacRuleSet;
    }

    @Override
    public RbacRuleSet getRbacRuleSet(FilterInfo<Predicate<RbacRule>> filterInfo) {
        final RbacRuleSet result;
        if (filterInfo != null) {
            final Predicate<RbacRule> filter = filterInfo.getFilter();
            result = new RbacRuleSet(rbacRuleSet.getRules().stream().filter(filter).collect(Collectors.toSet()));
        } else {
            result = new RbacRuleSet(rbacRuleSet.getRules());
        }
        return result;
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
