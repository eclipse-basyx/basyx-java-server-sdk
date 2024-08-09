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

public class RbacRuleAdapter {
	
	private TargetInformationAdapter targetInformationAdapter;
	
	public RbacRuleAdapter(TargetInformationAdapter targetInformationAdapter) {
		this.targetInformationAdapter = targetInformationAdapter;
	}
	
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
	
	public RbacRule adapt(SubmodelElementCollection rbacRule) {
		
		Property role = (Property) rbacRule.getValue().stream().filter(sme -> sme.getIdShort().equals("role")).findAny().get();
		
		SubmodelElementList actionSML = (SubmodelElementList) rbacRule.getValue().stream().filter(sme -> sme.getIdShort().equals("action")).findAny().get();
		
		List<Action> actions = actionSML.getValue().stream().map(Property.class::cast).map(actionProperty -> actionProperty.getValue()).map(Action::fromString) .collect(Collectors.toList());
		
		SubmodelElementCollection targetInformationSMC = (SubmodelElementCollection) rbacRule.getValue().stream().filter(sme -> sme.getIdShort().equals("targetInformation")).findAny().get();
		
		TargetInformation targetInformation = targetInformationAdapter.adapt(targetInformationSMC);
		
		return new RbacRule(role.getValue(), actions, targetInformation);
	}
	
	private Property transform(Action action) {
		return new DefaultProperty.Builder().value(action.toString()).build();
	}

}
