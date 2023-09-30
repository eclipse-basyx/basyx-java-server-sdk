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

package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.authorization;

import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.ConceptDescriptionRepositoryFactory;
import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.feature.ConceptDescriptionRepositoryFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * Repository feature for the authorization on the concept description level.
 * 
 * @author wege
 */
@ConditionalOnExpression("#{${" + AuthorizationConceptDescriptionRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.authorization.enabled:false}}")
@Component
public class AuthorizationConceptDescriptionRepositoryFeature<ConceptDescriptionFilterType> implements ConceptDescriptionRepositoryFeature<ConceptDescriptionFilterType> {
	public final static String FEATURENAME = "basyx.conceptdescriptionrepository.feature.authorization";

	@Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.authorization.enabled:false}}")
	private boolean enabled;

	@Autowired
	private final PermissionResolver<ConceptDescriptionFilterType> permissionResolver;

	@Autowired
	public AuthorizationConceptDescriptionRepositoryFeature(PermissionResolver<ConceptDescriptionFilterType> permissionResolver) {
		this.permissionResolver = permissionResolver;
	}

	@Override
	public ConceptDescriptionRepositoryFactory<ConceptDescriptionFilterType> decorate(ConceptDescriptionRepositoryFactory<ConceptDescriptionFilterType> aasServiceFactory) {
		return new AuthorizationConceptDescriptionRepositoryFactory<>(aasServiceFactory, permissionResolver);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public String getName() {
		return "ConceptDescriptionRepository Authorization";
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
