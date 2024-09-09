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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;

/**
 * 
 * An interface for adapting {@link RbacRule} to its
 * {@link SubmodelElementCollection} equivalent and vice-versa.
 * 
 * @author danish
 * 
 */
public class RbacRuleAdapter {

	private TargetInformationAdapter targetInformationAdapter;

	public RbacRuleAdapter(TargetInformationAdapter targetInformationAdapter) {
		this.targetInformationAdapter = targetInformationAdapter;
	}

	/**
	 * Adapts the {@link RbacRule} into {@link SubmodelElementCollection} rbac rule
	 * 
	 * @param rbacRule
	 * @param rbacRuleKey
	 * @return rbacRule SMC
	 */
	public SubmodelElementCollection adapt(RbacRule rbacRule, String rbacRuleKey) {

		SubmodelElementCollection rule = new DefaultSubmodelElementCollection.Builder().idShort(rbacRuleKey).build();

		Property role = new DefaultProperty.Builder().idShort("role").value(rbacRule.getRole()).build();
		SubmodelElementList action = new DefaultSubmodelElementList.Builder().idShort("action").build();

		List<SubmodelElement> actions = rbacRule.getAction().stream().map(this::transform).collect(Collectors.toList());
		action.setValue(actions);

		SubmodelElementCollection targetInformation = targetInformationAdapter.adapt(rbacRule.getTargetInformation());

		rule.setValue(Arrays.asList(role, action, targetInformation));

		return rule;
	}

	/**
	 * Adapts the {@link SubmodelElementCollection} rbac rule into {@link RbacRule}
	 * 
	 * @param rbacRule
	 *            SMC
	 * @return rbacRule
	 */
	public RbacRule adapt(SubmodelElementCollection rbacRule) {

		Property role = (Property) rbacRule.getValue().stream().filter(sme -> sme.getIdShort().equals("role")).findAny().get();

		SubmodelElementList actionSML = (SubmodelElementList) rbacRule.getValue().stream().filter(sme -> sme.getIdShort().equals("action")).findAny().get();

		List<Action> actions = actionSML.getValue().stream().map(Property.class::cast).map(actionProperty -> actionProperty.getValue()).map(Action::fromString).collect(Collectors.toList());

		SubmodelElementCollection targetInformationSMC = (SubmodelElementCollection) rbacRule.getValue().stream().filter(sme -> sme.getIdShort().equals("targetInformation")).findAny().get();

		TargetInformation targetInformation = targetInformationAdapter.adapt(targetInformationSMC);

		return new RbacRule(role.getValue(), actions, targetInformation);
	}

	private Property transform(Action action) {
		return new DefaultProperty.Builder().value(action.toString()).build();
	}

}
