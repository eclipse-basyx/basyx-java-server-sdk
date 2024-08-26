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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization.rbac;

import org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization.SubmodelRegistryTargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;

import java.util.List;

/**
 * Verifies the {@link SubmodelRegistryTargetInformation} against the {@link RbacRule}
 *
 * @author danish
 */
public class SubmodelRegistryTargetPermissionVerifier implements TargetPermissionVerifier<SubmodelRegistryTargetInformation> {

	public static final String ALL_ALLOWED_WILDCARD = "*";

	@Override
	public boolean isVerified(RbacRule rbacRule, SubmodelRegistryTargetInformation targetInformation) {
		List<String> targetInformationSubmodelIds = targetInformation.getSubmodelIds();

		SubmodelRegistryTargetInformation rbacRuleSubmodelTargetInformation = (SubmodelRegistryTargetInformation) rbacRule.getTargetInformation();

		List<String> rbacRuleSubmodelIds = rbacRuleSubmodelTargetInformation.getSubmodelIds();

		return areSubmodelsAllowed(rbacRuleSubmodelIds, targetInformationSubmodelIds);
	}

	private boolean areSubmodelsAllowed(List<String> rbacRuleSubmodelIds, List<String> targetInformationSubmodelIds) {
		return allSubmodelsAllowed(rbacRuleSubmodelIds) || rbacRuleSubmodelIds.containsAll(targetInformationSubmodelIds);
	}

	private boolean allSubmodelsAllowed(List<String> rbacRuleSubmodelIds) {

		return rbacRuleSubmodelIds.size() == 1 && rbacRuleSubmodelIds.get(0).equals(ALL_ALLOWED_WILDCARD);
	}

}
