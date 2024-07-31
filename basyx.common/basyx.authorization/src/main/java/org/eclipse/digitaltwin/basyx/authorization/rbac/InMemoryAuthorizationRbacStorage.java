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

import java.util.Map;

/**
 * InMemory implementation of the {@link RbacStorage}
 * 
 * @author danish
 */
public class InMemoryAuthorizationRbacStorage implements RbacStorage {
    private final Map<String, RbacRule> rbacRules;

    public InMemoryAuthorizationRbacStorage(Map<String, RbacRule> rbacRules) {
        this.rbacRules = rbacRules;
    }

    public Map<String, RbacRule> getRbacRules() {       
        return rbacRules;
    }

    public void addRule(RbacRule rbacRule) {
    	
    	rbacRule.getAction().stream().map(action -> RbacRuleKeyGenerator.generateKey(rbacRule.getRole(), action.toString(), rbacRule.getTargetInformation().getClass().getName())).filter(key -> !rbacRules.containsKey(key)).map(key -> rbacRules.put(key, rbacRule));
    }

	public void removeRule(String key) {
		if (!exist(key))
			throw new RuntimeException("Rule doesn't exist in policy store");
		
		rbacRules.remove(key);
    }
	
	@Override
	public RbacRule getRbacRule(String key) {
		if (!exist(key))
			throw new RuntimeException("Rule doesn't exist in policy store");
		
		return rbacRules.get(key);
	}
	
	@Override
	public boolean exist(String key) {
		return rbacRules.containsKey(key);
	}

}
