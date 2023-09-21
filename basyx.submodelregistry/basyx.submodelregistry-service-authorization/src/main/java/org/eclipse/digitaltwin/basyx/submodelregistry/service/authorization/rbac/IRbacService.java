package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleSet;

public interface IRbacService {
    public RbacRuleSet getRbacRuleSet();
    public void addRule(RbacRule rbacRule);
    public void removeRule(RbacRule rbacRule);
}
