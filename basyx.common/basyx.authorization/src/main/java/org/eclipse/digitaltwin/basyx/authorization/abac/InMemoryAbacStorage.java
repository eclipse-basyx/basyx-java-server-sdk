package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.List;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;

/**
 * InMemory implementation of the {@link RbacStorage}
 * 
 * @author danish
 */
public class InMemoryAbacStorage implements AbacStorage {
    private final AllAccessPermissionRules abacRules;

    public InMemoryAbacStorage(AllAccessPermissionRules abacRules) {
        this.abacRules = abacRules;
    }

    @Override
    public List<AccessPermissionRule> getAbacRules() {       
        return abacRules.getRules();
    }

	@Override
	public void addRule(AccessPermissionRule abacRule) {
		abacRules.getRules().add(abacRule);
		
	}

}

