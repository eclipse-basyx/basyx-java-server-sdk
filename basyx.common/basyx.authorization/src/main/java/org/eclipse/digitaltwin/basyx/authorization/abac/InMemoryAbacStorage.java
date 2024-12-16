package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.List;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;

/**
 * InMemory implementation of the {@link RbacStorage}
 * 
 * @author danish
 */
public class InMemoryAbacStorage implements AbacStorage {
    private final List<AllAccessPermissionRule> abacRules;

    public InMemoryAbacStorage(List<AllAccessPermissionRule> abacRules) {
        this.abacRules = abacRules;
    }

    @Override
    public List<AllAccessPermissionRule> getAbacRules() {       
        return abacRules;
    }

	@Override
	public void addRule(AllAccessPermissionRule abacRule) {
		abacRules.add(abacRule);
		
	}

}

