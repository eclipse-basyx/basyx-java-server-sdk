package org.eclipse.digitaltwin.basyx.authorization.rbac;

public interface RbacPermissionResolver<T extends TargetInformation> {

	public boolean hasPermission(Action action, T targetInformation);
	
}
