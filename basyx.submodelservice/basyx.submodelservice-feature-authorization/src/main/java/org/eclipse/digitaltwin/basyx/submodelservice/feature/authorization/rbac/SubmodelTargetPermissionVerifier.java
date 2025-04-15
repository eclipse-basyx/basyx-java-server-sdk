/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.rbac;

import java.util.List;

import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.submodelservice.feature.authorization.SubmodelTargetInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verifies the {@link SubmodelTargetInformation} against the {@link RbacRule}
 * 
 * @author danish
 */
public class SubmodelTargetPermissionVerifier implements TargetPermissionVerifier<SubmodelTargetInformation> {

	private final Logger log = LoggerFactory.getLogger(SubmodelTargetPermissionVerifier.class);
	
	private static final String ALL_ALLOWED_WILDCARD = "*";

	@Override
	public boolean isVerified(RbacRule rbacRule, SubmodelTargetInformation targetInformation) {
		
		List<String> targetInformationSubmodelIds = targetInformation.getSubmodelIds();
		List<String> targetInformationSubmodelElementIdShortPath = targetInformation.getSubmodelElementIdShortPaths();
		
		SubmodelTargetInformation rbacRuleSubmodelTargetInformation = (SubmodelTargetInformation) rbacRule.getTargetInformation();
		
		List<String> rbacRuleSubmodelIds = rbacRuleSubmodelTargetInformation.getSubmodelIds();
		List<String> rbacRuleSubmodelElementIdShortPath = rbacRuleSubmodelTargetInformation.getSubmodelElementIdShortPaths();

		if (areElementsAllowed(rbacRuleSubmodelIds, targetInformationSubmodelIds))
			return areElementsAllowed(rbacRuleSubmodelElementIdShortPath, targetInformationSubmodelElementIdShortPath);
		
		return false;
	}
	
	private boolean areElementsAllowed(List<String> rbacRuleIds, List<String> targetInformationIds) {
		boolean toReturn = allElementsAllowed(rbacRuleIds) || rbacRuleIds.containsAll(targetInformationIds);
		log.info("Are elements allowed? present: " + rbacRuleIds + " - requested: " + targetInformationIds);
		return toReturn;
	}
	
	private boolean allElementsAllowed(List<String> rbacRuleIds) {
		
		return rbacRuleIds.size() == 1 && rbacRuleIds.get(0).equals(ALL_ALLOWED_WILDCARD);
	}

}
