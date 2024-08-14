/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization.rbac;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasenvironment.feature.authorization.AasEnvironmentTargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;

/**
 * Verifies the {@link AasEnvironmentTargetInformation} against the {@link RbacRule}
 * 
 * @author danish
 */
public class AasEnvironmentTargetPermissionVerifier implements TargetPermissionVerifier<AasEnvironmentTargetInformation> {

	private static final String ALL_ALLOWED_WILDCARD = "*";
	
	@Override
	public boolean isVerified(RbacRule rbacRule, AasEnvironmentTargetInformation targetInformation) {
		List<String> targetInformationShellIds = targetInformation.getAasIds();
		List<String> targetInformationSubmdelIds = targetInformation.getSubmodelIds();
		
		AasEnvironmentTargetInformation rbacRuleAasEnvironmentTargetInformation = (AasEnvironmentTargetInformation) rbacRule.getTargetInformation();
		
		List<String> rbacRuleShellIds = rbacRuleAasEnvironmentTargetInformation.getAasIds();
		List<String> rbacRuleSubmdelIds = rbacRuleAasEnvironmentTargetInformation.getSubmodelIds();
		
		return areShellsAllowed(rbacRuleShellIds, targetInformationShellIds) && areSubmodelsAllowed(rbacRuleSubmdelIds, targetInformationSubmdelIds);
	}

	private boolean areShellsAllowed(List<String> rbacRuleShellIds, List<String> targetInformationShellIds) {
		
		return allShellsAllowed(rbacRuleShellIds) || rbacRuleShellIds.containsAll(targetInformationShellIds);	
	}
	
	private boolean areSubmodelsAllowed(List<String> rbacRuleSubmdelIds, List<String> targetInformationSubmdelIds) {
		
		return allSubmodelsAllowed(rbacRuleSubmdelIds) || rbacRuleSubmdelIds.containsAll(targetInformationSubmdelIds);	
	}

	private boolean allSubmodelsAllowed(List<String> rbacRuleSubmdelIds) {
		
		return rbacRuleSubmdelIds.size() == 1 && rbacRuleSubmdelIds.get(0).equals(ALL_ALLOWED_WILDCARD);
	}

	private boolean allShellsAllowed(List<String> rbacRuleShellIds) {
		
		return rbacRuleShellIds.size() == 1 && rbacRuleShellIds.get(0).equals(ALL_ALLOWED_WILDCARD);
	}

}
