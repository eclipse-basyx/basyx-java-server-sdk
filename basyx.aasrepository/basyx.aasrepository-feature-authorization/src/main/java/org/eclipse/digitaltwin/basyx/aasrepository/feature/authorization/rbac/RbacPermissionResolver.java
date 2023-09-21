package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.core.filtering.FilterInfo;

public interface RbacPermissionResolver<RbacRuleFilterType> {
    public FilterInfo<RbacRuleFilterType> getGetRbacRuleSetFilterInfo();
    public void addRule(RbacRule rbacRule);
    public void removeRule(RbacRule rbacRule);
}
