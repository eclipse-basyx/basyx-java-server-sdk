package org.eclipse.digitaltwin.basyx.submodelservice.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.rbac.IRbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.stream.Collectors;

@ConditionalOnExpression(value = "'${basyx.submodelservice.feature.authorization.type}' == 'rbac' and '${basyx.submodelservice.backend}'.equals('inMemory')")
@Service
public class InMemoryAuthorizationRbacStorage implements IRbacStorage<Predicate<RbacRule>> {
    private final RbacRuleSet rbacRuleSet;

    public InMemoryAuthorizationRbacStorage(RbacRuleSet rbacRuleSet) {
        this.rbacRuleSet = rbacRuleSet;
    }

    @Override
    public RbacRuleSet getRbacRuleSet(FilterInfo<Predicate<RbacRule>> filterInfo) {
        RbacRuleSet result = rbacRuleSet;
        if (filterInfo != null) {
            result = new RbacRuleSet(result.getRules().stream()
                    .filter(rbacRule -> filterInfo.getFilter().test(rbacRule))
                    .collect(Collectors.toSet()));
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
