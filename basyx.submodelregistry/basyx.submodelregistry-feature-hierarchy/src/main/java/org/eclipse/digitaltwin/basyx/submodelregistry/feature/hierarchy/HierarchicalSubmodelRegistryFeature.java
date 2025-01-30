/*******************************************************************************
 * Copyright (C) 2025 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.hierarchy;

import org.eclipse.digitaltwin.basyx.common.hierarchy.CommonHierarchyProperties;
import org.eclipse.digitaltwin.basyx.common.hierarchy.delegation.DelegationStrategy;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorageFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Hierarchical {@link SubmodelRegistryStorage} feature
 * 
 * When this feature is enabled, retrieval requests will be delegated to the
 * next SubmodelRegistry.
 * 
 * The next SubmodelRegistry is selected via a {@link DelegationStrategy}
 * 
 * @author mateusmolina
 */
@Component
@ConditionalOnExpression("${" + CommonHierarchyProperties.HIERARCHY_FEATURE_ENABLED + ":false}")
@Order(1)
public class HierarchicalSubmodelRegistryFeature implements SubmodelRegistryStorageFeature {
	@Value("${" + CommonHierarchyProperties.HIERARCHY_FEATURE_ENABLED + "}")
	private boolean enabled;

	private DelegationStrategy delegationStrategy;

	public HierarchicalSubmodelRegistryFeature(DelegationStrategy delegationStrategy) {
		this.delegationStrategy = delegationStrategy;
	}

	@Override
	public SubmodelRegistryStorage decorate(SubmodelRegistryStorage storage) {
		return new HierarchicalSubmodelRegistryStorage(storage, delegationStrategy);
	}

	@Override
	public String getName() {
		return "SubmodelRegistry Hierarchy";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

}