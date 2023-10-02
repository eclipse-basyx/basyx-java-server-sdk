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
package org.eclipse.digitaltwin.basyx.aasrepository.feature.authorization;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.AasRepositoryFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@ConditionalOnExpression("#{${" + AuthorizationAasRepositoryFeature.FEATURENAME + ".enabled:false} or ${basyx.feature.authorization.enabled:false}}")
@Order(0)
@Component
public class AuthorizationAasRepositoryFeature implements AasRepositoryFeature {
    public final static String FEATURENAME = "basyx.aasrepository.feature.authorization";

    @Value("#{${" + FEATURENAME + ".enabled:false} or ${basyx.feature.authorization.enabled:false}}")
    private boolean enabled;

    @Autowired
    public AuthorizationAasRepositoryFeature() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void cleanUp() {

    }

    @Override
    public String getName() {
        return "AasRepository Authorization";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Autowired
    private PermissionResolver<?, ?> permissionResolver;

    @Override
    public AasRepositoryFactory decorate(AasRepositoryFactory aasRepositoryFactory) {
        return new AuthorizationAasRepositoryFactory(aasRepositoryFactory, permissionResolver);
    }
}
