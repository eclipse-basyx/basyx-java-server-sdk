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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.rbac;

import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization.ConceptDescriptionTargetInformation;

import java.util.List;

/**
 * Verifies the {@link ConceptDescriptionTargetInformation} against the {@link RbacRule}
 * 
 * @author danish
 */
public class ConceptDescriptionTargetPermissionVerifier implements TargetPermissionVerifier<ConceptDescriptionTargetInformation> {

	private static final String ALL_ALLOWED_WILDCARD = "*";
	
	@Override
	public boolean isVerified(RbacRule rbacRule, ConceptDescriptionTargetInformation targetInformation) {
		List<String> targetInformationConceptDescriptionIds = targetInformation.getConceptDescriptionIds();
		
		ConceptDescriptionTargetInformation rbacRuleConceptDescriptionTargetInformation = (ConceptDescriptionTargetInformation) rbacRule.getTargetInformation();

		List<String> rbacRuleConceptDescriptionIds = rbacRuleConceptDescriptionTargetInformation.getConceptDescriptionIds();

		return areConceptDescriptionsAllowed(rbacRuleConceptDescriptionIds, targetInformationConceptDescriptionIds);
	}

	private boolean areConceptDescriptionsAllowed(List<String> rbacRuleConceptDescriptionIds, List<String> targetInformationConceptDescriptionIds) {

		return allConceptDescriptionsAllowed(rbacRuleConceptDescriptionIds) || rbacRuleConceptDescriptionIds.containsAll(targetInformationConceptDescriptionIds);
	}

	private boolean allConceptDescriptionsAllowed(List<String> rbacRuleShellIds) {

		return rbacRuleShellIds.size() == 1 && rbacRuleShellIds.get(0).equals(ALL_ALLOWED_WILDCARD);
	}

}
