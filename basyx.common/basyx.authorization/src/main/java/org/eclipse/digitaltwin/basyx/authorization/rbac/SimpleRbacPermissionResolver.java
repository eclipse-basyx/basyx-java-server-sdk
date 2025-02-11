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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract permission resolver for {@link TargetInformation}
 * 
 * @param <T>
 * 
 * @author danish
 */
public class SimpleRbacPermissionResolver<T extends TargetInformation> implements RbacPermissionResolver<T> {

	private Logger logger = LoggerFactory.getLogger(SimpleRbacPermissionResolver.class);

	private RbacStorage rbacStorage;
	private RoleProvider roleAuthenticator;
	private TargetPermissionVerifier<T> targetPermissionVerifier;

	public SimpleRbacPermissionResolver(RbacStorage rbacStorage, RoleProvider roleAuthenticator, TargetPermissionVerifier<T> targetPermissionVerifier) {
		super();
		this.rbacStorage = rbacStorage;
		this.roleAuthenticator = roleAuthenticator;
		this.targetPermissionVerifier = targetPermissionVerifier;
	}

	public RbacStorage getRbacStorage() {
		return rbacStorage;
	}

	public RoleProvider getRoleProvider() {
		return roleAuthenticator;
	}

	/**
	 * Checks whether the {@link TargetInformation} has sufficient permission for
	 * the {@link Action} based on the {@link RbacRule}
	 * 
	 * @param action
	 * @param targetInformation
	 * 
	 * @return 
	 */
	public boolean hasPermission(final Action action, final T targetInformation) {
		final Optional<RbacRule> matchingRule = getMatchingRules(roleAuthenticator.getRoles(), action, targetInformation).findAny();
		
		logger.info("roles: {}, action: {}, targetInfo: {} - matching-rule?: {}", roleAuthenticator.getRoles(), action, targetInformation, matchingRule);
		
		return matchingRule.isPresent();
	}
	
	public List<TargetInformation> getMatchingTargetInformationInRules(final Action action, final T targetInformation) {
		
		List<String> roles = roleAuthenticator.getRoles();
		
		List<RbacRule> filteredRbacRulesForTargetInfos = roles.stream().map(role -> generateKey(role, action.toString(), targetInformation.getClass().getName())).filter(Objects::nonNull).filter(this::exist).map(this::getRbacRule)
				.filter(Objects::nonNull).collect(Collectors.toList());
		
		return filteredRbacRulesForTargetInfos.stream().map(rbacRule -> rbacRule.getTargetInformation()).collect(Collectors.toList());
	}

	private Stream<RbacRule> getMatchingRules(final List<String> roles, final Action action, final T targetInformation) {
		
		List<RbacRule> filteredRbacRulesForTargetInfos = roles.stream().map(role -> RbacRuleKeyGenerator.generateKey(role, action.toString(), targetInformation.getClass().getName())).filter(rbacStorage::exist).map(rbacStorage::getRbacRule).collect(Collectors.toList());
		
		return filteredRbacRulesForTargetInfos.stream().filter(rbacRule -> checkRbacRuleMatchesTargetInfo(rbacRule, targetInformation));
	}

	private boolean checkRbacRuleMatchesTargetInfo(final RbacRule rbacRule, final T targetInformation) {
		
		return targetPermissionVerifier.isVerified(rbacRule, targetInformation);
	}
	
	private String generateKey(String role, String action, String targetClassName) {
		try {
			return RbacRuleKeyGenerator.generateKey(role, action, targetClassName);
		} catch (Exception e) {
			logger.error("Error generating Rbac Rule Key for role: '{}' Action: '{}' and Target Information Class: '{}'", role, action, targetClassName, e);
			return null;
		}
	}

	private boolean exist(String key) {
		try {
			return rbacStorage.exist(key);
		} catch (Exception e) {
			logger.error("Error checking existence of Rbac Rule for key: {}", key, e);
			return false;
		}
	}

	private RbacRule getRbacRule(String key) {
		try {
			return rbacStorage.getRbacRule(key);
		} catch (Exception e) {
			logger.error("Error retrieving the Rbac Rule for key: {}", key, e);
			return null;
		}
	}
	
}
