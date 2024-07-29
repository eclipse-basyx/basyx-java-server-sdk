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

package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;
import org.eclipse.digitaltwin.basyx.submodelrepository.client.ConnectedSubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.client.ConnectedSubmodelService;

/**
 * InMemory implementation of the {@link RbacStorage}
 * 
 * @author danish
 */
public class SubmodelAuthorizationRbacStorage implements RbacStorage {
    private final List<RbacRule> rbacRules;
    private final TargetInformationAdapter targetInformationAdapter;
    private RbacRuleAdapter ruleAdapter;
    private ConnectedSubmodelRepository submodelRepository;

    public SubmodelAuthorizationRbacStorage(ConnectedSubmodelRepository smRepo, List<RbacRule> rbacRuleList, TargetInformationAdapter targetInformationAdapter) {
        this.rbacRules = rbacRuleList;
        this.targetInformationAdapter = targetInformationAdapter;
        ruleAdapter = new RbacRuleAdapter(targetInformationAdapter);
        this.submodelRepository = submodelRepository;
        
        initializeRbacRules(rbacRuleList, targetInformationAdapter);
    }

	public List<RbacRule> getRbacRules() {       
        return rbacRules;
    }

    public void addRule(RbacRule rbacRule) {
    	SubmodelElementCollection rule = ruleAdapter.adapt(rbacRule);
    	
    	ConnectedSubmodelService submodelService = submodelRepository.getConnectedSubmodelService("submodelId");
    	
    	submodelService.get
        rbacRules.add(rbacRule);
    }

    public void removeRule(RbacRule rbacRule) {
        rbacRules.remove(rbacRule);
    }
    
    private void initializeRbacRules(List<RbacRule> rbacRuleList, TargetInformationAdapter targetInformationAdapter) {
    	rbacRuleList.stream().forEach(rule -> addRule(rule));
	}
}
