package org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultProperty;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultSubmodelElementList;
import org.eclipse.digitaltwin.basyx.authorization.rbac.Action;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleKeyGenerator;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetInformation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class RbacRuleAdapterTest {

    @Mock
    private TargetInformationAdapter targetInformationAdapter;
    
    @Mock
    private TargetInformation targetInformation;

    private RbacRuleAdapter rbacRuleAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rbacRuleAdapter = new RbacRuleAdapter(targetInformationAdapter);
    }
    
    @Test
    public void adaptRbacRuleToSubmodelElementCollection() {

        String role = "admin";
        List<Action> actions = Arrays.asList(Action.READ);
        
        String rbacRuleKey = RbacRuleKeyGenerator.generateKey(role, actions.get(0).toString(), targetInformation.getClass().getName());

        RbacRule rbacRule = new RbacRule(role, actions, targetInformation);
        
        SubmodelElementCollection expectedSMCRule = createDummySMC(rbacRule, rbacRuleKey);
        
        SubmodelElementCollection expectedTargetInformation = createDummyTargetInformation();
        
        when(targetInformationAdapter.adapt(targetInformation)).thenReturn(expectedTargetInformation);

        SubmodelElementCollection actualSMCRule = rbacRuleAdapter.adapt(rbacRule, rbacRuleKey);

        assertEquals(rbacRuleKey, actualSMCRule.getIdShort());

        List<SubmodelElement> elements = actualSMCRule.getValue();
        assertEquals(3, elements.size());

        Property roleProperty = (Property) elements.stream().filter(sme -> sme.getIdShort().equals("role")).findAny().get();
        assertEquals(role, roleProperty.getValue());

        SubmodelElementList actionList = (SubmodelElementList) elements.stream().filter(sme -> sme.getIdShort().equals("action")).findAny().get();
        List<String> actionValues = actionList.getValue().stream().map(Property.class::cast).map(Property::getValue).map(String::valueOf).collect(Collectors.toList());
        assertEquals(Arrays.asList("READ"), actionValues);

        SubmodelElementCollection actualTargetInformation = (SubmodelElementCollection) elements.stream().filter(sme -> sme.getIdShort().equals("targetInformation")).findAny().get();
        assertSame(expectedTargetInformation, actualTargetInformation);
        
        assertEquals(expectedSMCRule, actualSMCRule);
    }
    
    @Test
    public void adaptSubmodelElementCollectionToRbacRule() {
        
    	RbacRule expectedRbacRule = createDummyRbacRule();
    	
    	String rbacRuleKey = RbacRuleKeyGenerator.generateKey(expectedRbacRule.getRole(), expectedRbacRule.getAction().get(0).toString(), expectedRbacRule.getTargetInformation().getClass().getName());

        when(targetInformationAdapter.adapt(createDummyTargetInformation())).thenReturn(targetInformation);

        SubmodelElementCollection rbacRuleSMC = createDummySMC(expectedRbacRule, rbacRuleKey);

        RbacRule actualRbacRule = rbacRuleAdapter.adapt(rbacRuleSMC);

        assertEquals(expectedRbacRule, actualRbacRule);
    }
    
    @Test
    public void targetInformationAdapterInvoked() {
        String rbacRuleKey = "testRuleKey";
        String role = "admin";
        List<Action> actions = Arrays.asList(Action.READ);

        RbacRule rbacRule = new RbacRule(role, actions, targetInformation);

        SubmodelElementCollection expectedTargetInformation = createDummyTargetInformation();
        
        when(targetInformationAdapter.adapt(targetInformation)).thenReturn(expectedTargetInformation);

        rbacRuleAdapter.adapt(rbacRule, rbacRuleKey);

        verify(targetInformationAdapter, times(1)).adapt(targetInformation);
    }

    
    private SubmodelElementCollection createDummySMC(RbacRule rbacRule, String rbacRuleKey) {
    	
    	SubmodelElementCollection rule = new DefaultSubmodelElementCollection.Builder().idShort(rbacRuleKey).build();
		
		Property role = new DefaultProperty.Builder().idShort("role").value(rbacRule.getRole()).build();
		SubmodelElementList action = new DefaultSubmodelElementList.Builder().idShort("action").build();
		
		List<SubmodelElement> actions = rbacRule.getAction().stream().map(this::transform).collect(Collectors.toList());
		action.setValue(actions);
		
		SubmodelElementCollection targetInformation = createDummyTargetInformation();
		
		rule.setValue(Arrays.asList(role, action, targetInformation));
		
		return rule;
    }
    
    private RbacRule createDummyRbacRule() {
    	
    	String role = "admin";
        List<Action> actions = Arrays.asList(Action.READ);
		
		return new RbacRule(role, actions, targetInformation);
    }

	private SubmodelElementCollection createDummyTargetInformation() {
		SubmodelElementCollection targetInformation = new DefaultSubmodelElementCollection.Builder().idShort("targetInformation").build();
		
		SubmodelElementList targetInformationSML = new DefaultSubmodelElementList.Builder().idShort("aasIds").build();
		
		Property targetInformationProperty = new DefaultProperty.Builder().value("dummyAasId").build();
		
		targetInformationSML.setValue(Arrays.asList(targetInformationProperty));
		
		targetInformation.setValue(Arrays.asList(targetInformationSML));
		
		return targetInformation;
	}
    
    private Property transform(Action action) {
		return new DefaultProperty.Builder().value(action.toString()).build();
	}

}
