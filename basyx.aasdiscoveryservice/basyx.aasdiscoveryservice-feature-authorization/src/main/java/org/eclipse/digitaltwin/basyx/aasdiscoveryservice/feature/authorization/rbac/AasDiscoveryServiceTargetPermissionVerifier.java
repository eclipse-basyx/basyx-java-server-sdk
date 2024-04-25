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

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.feature.authorization.rbac;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.model.AssetLink;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.feature.authorization.AasDiscoveryServiceTargetInformation;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;

/**
 * Verifies the {@link AasDiscoveryServiceTargetInformation} against the
 * {@link RbacRule}
 *
 * @author mateusmolina
 *
 */
public class AasDiscoveryServiceTargetPermissionVerifier implements TargetPermissionVerifier<AasDiscoveryServiceTargetInformation> {
	private static final String ALL_ALLOWED_WILDCARD = "*";

	@Override
	public boolean isVerified(RbacRule rbacRule, AasDiscoveryServiceTargetInformation targetInformation) {
		List<String> targetInformationShellIds = targetInformation.getAasIds();
		List<AssetLink> targetInformationAssetLinks = targetInformation.getAssetLinks();

		AasDiscoveryServiceTargetInformation rbacRuleAasDiscoveryServiceTargetInformation = (AasDiscoveryServiceTargetInformation) rbacRule.getTargetInformation();

		List<String> rbacRuleShellIds = rbacRuleAasDiscoveryServiceTargetInformation.getAasIds();
		List<AssetLink> rbacRuleAssetLinks = rbacRuleAasDiscoveryServiceTargetInformation.getAssetLinks();

		if (rbacRuleShellIds == null || rbacRuleShellIds.isEmpty())
			return areAssetLinksAllowed(rbacRuleAssetLinks, targetInformationAssetLinks);

		if (rbacRuleAssetLinks == null || rbacRuleAssetLinks.isEmpty())
			return areShellsAllowed(rbacRuleShellIds, targetInformationShellIds);

		return areShellsAllowed(rbacRuleShellIds, targetInformationShellIds) && areAssetLinksAllowed(rbacRuleAssetLinks, targetInformationAssetLinks);
	}

	private boolean areShellsAllowed(List<String> rbacRuleShellIds, List<String> targetInformationShellIds) {

		return allShellsAllowed(rbacRuleShellIds) || rbacRuleShellIds.containsAll(targetInformationShellIds);
	}

	private boolean areAssetLinksAllowed(List<AssetLink> rbacRuleAssetLinks, List<AssetLink> targetInformationAssetLinks) {

		return allAssetLinksAllowed(rbacRuleAssetLinks) || rbacRuleAssetLinks.containsAll(targetInformationAssetLinks);
	}

	private boolean allAssetLinksAllowed(List<AssetLink> rbacRuleAssetLinks) {

		return rbacRuleAssetLinks.size() == 1 && rbacRuleAssetLinks.get(0).getName().equals(ALL_ALLOWED_WILDCARD) && rbacRuleAssetLinks.get(0).getValue().equals(ALL_ALLOWED_WILDCARD);
	}

	private boolean allShellsAllowed(List<String> rbacRuleShellIds) {

		return rbacRuleShellIds.size() == 1 && rbacRuleShellIds.get(0).equals(ALL_ALLOWED_WILDCARD);
	}

}
