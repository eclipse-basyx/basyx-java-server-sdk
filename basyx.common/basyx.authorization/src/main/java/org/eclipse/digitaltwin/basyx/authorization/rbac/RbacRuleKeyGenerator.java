package org.eclipse.digitaltwin.basyx.authorization.rbac;

public class RbacRuleKeyGenerator {
	
	public static String generateKey(String role, String action, String clazz) {
		return String.valueOf((role + action + clazz).hashCode());
	}

}
