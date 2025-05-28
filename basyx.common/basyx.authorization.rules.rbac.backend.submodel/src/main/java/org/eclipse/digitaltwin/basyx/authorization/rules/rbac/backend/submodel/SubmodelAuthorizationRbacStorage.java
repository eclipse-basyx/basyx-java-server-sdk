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

package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleKeyGenerator;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InMemory implementation of the {@link RbacStorage}
 * 
 * @author danish
 */
public class SubmodelAuthorizationRbacStorage implements RbacStorage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SubmodelAuthorizationRbacStorage.class);
	private RbacRuleAdapter ruleAdapter;
	private ConnectedSubmodelService smService;

	public SubmodelAuthorizationRbacStorage(ConnectedSubmodelService smService, HashMap<String, RbacRule> initialRules, RbacRuleAdapter ruleAdapter) {
		this.ruleAdapter = ruleAdapter;
		this.smService = smService;

		initializeRbacRules(initialRules);
	}

	@Override
	public void addRule(RbacRule rbacRule) {

		List<SubmodelElementCollection> rbacRulesSMC = rbacRule.getAction().stream().map(action -> new RbacRule(rbacRule.getRole(), Arrays.asList(action), rbacRule.getTargetInformation()))
				.map(rule -> ruleAdapter.adapt(rule, createKey(rule))).collect(Collectors.toList());

		rbacRulesSMC.stream().forEach(rule -> {
			try{
				smService.getSubmodelElement(rule.getIdShort());
				LOGGER.warn("Rule with key " + rule.getIdShort() + " already exists. Skipping creation.");
			} catch (Exception e) {
				try {
					smService.createSubmodelElement(rule);
				} catch (Exception e2) {
					LOGGER.error("Exception while creating SubmodelElement for rule: " + rule.getIdShort() + ". Error: " + e2.getMessage());
				}
			}

		});
	}

	@Override
	public RbacRule getRbacRule(String key) {

		SubmodelElementCollection ruleSMC = (SubmodelElementCollection) smService.getSubmodelElement(key);

		return ruleAdapter.adapt(ruleSMC);
	}

	@Override
	public void removeRule(String key) {
		smService.deleteSubmodelElement(key);
	}

	@Override
	public boolean exist(String key) {

		try {
			smService.getSubmodelElement(key);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	public Map<String, RbacRule> getRbacRules() {

		return smService.getSubmodelElements(PaginationInfo.NO_LIMIT).getResult().stream().map(SubmodelElementCollection.class::cast).map(ruleAdapter::adapt).collect(Collectors.toMap(rbacRule -> createKey(rbacRule), rbacRule -> rbacRule));
	}

	private String createKey(RbacRule rbacRule) {

		return RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), rbacRule.getAction().get(0).toString(), rbacRule.getTargetInformation().getClass().getName());
	}

	private void initializeRbacRules(HashMap<String, RbacRule> initialRules) {
		initialRules.values().stream().forEach(rule -> addRule(rule));
	}
}
