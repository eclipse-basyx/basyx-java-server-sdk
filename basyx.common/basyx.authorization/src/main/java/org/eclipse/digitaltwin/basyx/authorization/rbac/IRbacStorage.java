package org.eclipse.digitaltwin.basyx.authorization.rbac;

import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

public interface IRbacStorage<RbacRuleFilterType> {
    public RbacRuleSet getRbacRuleSet(FilterInfo<RbacRuleFilterType> filterInfo);
    public void addRule(RbacRule rbacRule);
    public void removeRule(RbacRule rbacRule);
}
