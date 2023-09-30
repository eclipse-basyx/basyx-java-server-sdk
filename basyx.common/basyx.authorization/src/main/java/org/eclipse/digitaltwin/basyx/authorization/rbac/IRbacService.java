package org.eclipse.digitaltwin.basyx.authorization.rbac;

public interface IRbacService {
    public RbacRuleSet getRbacRuleSet();
    public void addRule(RbacRule rbacRule);
    public void removeRule(RbacRule rbacRule);
}
