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
package org.eclipse.digitaltwin.basyx.authorization.rbac;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Implementation of {@link IRbacRuleChecker} that works with a predefined
 * {@link RbacRuleSet}.
 *
 * @author wege
 */
public class PredefinedSetRbacRuleChecker implements IRbacRuleChecker {
	private static final Logger logger = LoggerFactory.getLogger(PredefinedSetRbacRuleChecker.class);
	private final RbacRuleSet rbacRuleSet;

	public PredefinedSetRbacRuleChecker(final RbacRuleSet rbacRuleSet) {
		this.rbacRuleSet = rbacRuleSet;
	}

	/**
	 * Checks for a given rbac tuple if it exists within the predefined set.
	 *
	 * @param roles
	 *            roles of the subject
	 * @param action
	 *            action which needs authorization
	 * @param targetInfo
	 *            target attributes
	 * @return true if the requested rbac tuple was found, false otherwise
	 */
	public boolean checkRbacRuleIsSatisfied(final List<String> roles, final String action, final ITargetInfo targetInfo) {
		final Optional<RbacRule> matchingRule = getMatchingRules(roles, action, targetInfo).findAny();
		logger.info("roles: {}, action: {}, targetInfo: {} - matching-rule?: {}", roles, action, targetInfo, matchingRule);
		return matchingRule.isPresent();
	}

	private Stream<RbacRule> getMatchingRules(final List<String> roles, final String action, final ITargetInfo targetInfo) {
		return this.rbacRuleSet.getRules().parallelStream()
				.filter(rbacRule -> checkRolesMatchRbacRule(rbacRule, roles))
				.filter(rbacRule -> checkActionMatchesRbacRule(rbacRule, action))
				.filter(rbacRule -> checkRbacRuleMatchesTargetInfo(rbacRule, targetInfo));
	}

	private boolean checkRolesMatchRbacRule(final RbacRule rbacRule, final List<String> roles) {
		return rbacRule.getRole().equals("*") || (roles != null && roles.stream().anyMatch(role -> rbacRule.getRole().equals(role)));
	}

	private boolean checkActionMatchesRbacRule(final RbacRule rbacRule, final String action) {
		return rbacRule.getAction().equals("*") || rbacRule.getAction().equals(action);
	}

	private boolean checkRbacRuleMatchesTargetInfo(final RbacRule rbacRule, final ITargetInfo matchTargetInfo) {
		final ITargetInfo rbacRuleTargetInfo = rbacRule.getTargetInfo();
		if (!rbacRuleTargetInfo.getClass().isAssignableFrom(matchTargetInfo.getClass())) {
			// return false if the type of the target is not the same or a subtype of the target info specified in the rbac rule
			// because, e.g., the BasyxObjectTargetInfo should not be a valid substitute for an RbacRuleTargetInfo
			return false;
		}

		final Map<String, String> matchTargetInfoMap = matchTargetInfo.toMap();
		final Map<String, String> rbacRuleTargetInfoMap = rbacRuleTargetInfo.toMap();
		for (final Map.Entry<String, String> matchTargetInfoMapEntry : matchTargetInfoMap.entrySet()) {
			final String key = matchTargetInfoMapEntry.getKey();
			final String matchTargetInfoSingleValue = matchTargetInfoMapEntry.getValue();
			final String rbacRuleValue = rbacRuleTargetInfoMap.get(key);

			if (!checkRegexStringMatch(rbacRuleValue, matchTargetInfoSingleValue)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkRegexStringMatch(final String actualString, final String requiredString) {
		if (requiredString == null) {
			return true;
		}
		if (actualString == null) {
			return false;
		}
		return actualString.equals("*") || requiredString.matches(actualString.replaceAll("\\*", "[A-Za-z0-9.]+"));
	}
}
