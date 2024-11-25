package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.List;
import java.util.Map;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRule;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacRuleKeyGenerator;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * InMemory implementation of the {@link RbacStorage}
 * 
 * @author danish
 */
public class InMemoryAbacStorage implements AbacStorage {
    private final List<AllRule> abacRules;

    public InMemoryAbacStorage(List<AllRule> abacRules) {
        this.abacRules = abacRules;
    }

    @Override
    public List<AllRule> getAbacRules() {       
        return abacRules;
    }

	@Override
	public void addRule(AllRule abacRule) {
		abacRules.add(abacRule);
		
	}

}

