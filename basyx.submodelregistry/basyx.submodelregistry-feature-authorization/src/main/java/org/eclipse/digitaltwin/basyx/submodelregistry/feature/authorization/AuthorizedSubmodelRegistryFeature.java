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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorage;
import org.eclipse.digitaltwin.basyx.submodelregistry.service.storage.SubmodelRegistryStorageFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Feature for authorized {@link SubmodelRegistryStorage}
 *
 * @author danish
 */
@Component
@ConditionalOnExpression("#{${" + CommonAuthorizationProperties.ENABLED_PROPERTY_KEY + ":false}}")
@Order(0)
public class AuthorizedSubmodelRegistryFeature implements SubmodelRegistryStorageFeature {
	
	@Value("${" + CommonAuthorizationProperties.ENABLED_PROPERTY_KEY + ":}")
	private boolean enabled;
	
	private RbacPermissionResolver<SubmodelRegistryTargetInformation> permissionResolver;

	@Autowired
	public AuthorizedSubmodelRegistryFeature(RbacPermissionResolver<SubmodelRegistryTargetInformation> permissionResolver) {
		this.permissionResolver = permissionResolver;
	}

	@Override
	public SubmodelRegistryStorage decorate(SubmodelRegistryStorage storage) {
		return new AuthorizedSubmodelRegistryStorage(storage, permissionResolver);
	}

	@Override
	public String getName() {
		return "SubmodelRegistry Authorization";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}