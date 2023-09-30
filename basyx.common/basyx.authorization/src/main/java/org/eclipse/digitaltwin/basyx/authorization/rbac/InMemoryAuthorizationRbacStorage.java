package org.eclipse.digitaltwin.basyx.authorization.rbac;

import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

import java.util.function.Predicate;
import java.util.stream.Collectors;

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
