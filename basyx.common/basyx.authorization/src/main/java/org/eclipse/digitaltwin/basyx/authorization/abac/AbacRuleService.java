package org.eclipse.digitaltwin.basyx.authorization.abac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AbacRuleService {

    @Autowired
    private AbacStorage repository;

    public List<AccessPermissionRule> getAbacRules() {
        return repository.getAbacRules();
    }

    public List<AccessPermissionRule> getFilteredAbacRules(RightsEnum right, Acl.Access access, ObjectItem objectItem) {
        return repository.getFilteredAbacRules(right, access, objectItem);
    }

    public void addRule(AccessPermissionRule abacRule) {
    	repository.addRule(abacRule);
	}

	public void removeRule(String ruleId) {
		repository.removeRule(ruleId);
	}

	public boolean exists(String ruleId) {
		
        return repository.exists(ruleId);
	}

	public AccessPermissionRule getRule(String id) {
		return repository.getRule(id);
	}
	
	public void update(String ruleId, AccessPermissionRule abacRule) {
		repository.update(ruleId, abacRule);
	}
}
