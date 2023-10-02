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
package org.eclipse.digitaltwin.basyx.aasservice.authorization;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationConfig;
import org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.digitaltwin.basyx.aasservice.authorization.AuthorizationConfig.ENABLED_PROPERTY_KEY;
import static org.eclipse.digitaltwin.basyx.aasservice.authorization.AuthorizationConfig.TYPE_PROPERTY_KEY;
import static org.eclipse.digitaltwin.basyx.aasservice.authorization.rbac.RbacStorageConfig.RULES_FILE_KEY;

/**
 * This maps the component-specific properties to the general authorization properties from the @{link org.eclipse.digitaltwin.basyx.authorization.AuthorizationConfig}.
 */
public class AuthorizationDynamicPropertiesListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        final ConfigurableEnvironment environment = event.getEnvironment();
        final boolean authorizationEnabled = Boolean.TRUE.equals(environment.getProperty(ENABLED_PROPERTY_KEY, Boolean.class));
        if (authorizationEnabled) {
            final String authorizationType = environment.getProperty(TYPE_PROPERTY_KEY);
            final String rbacRulesFile = environment.getProperty(RULES_FILE_KEY);

            final Map<String, Object> myMap = new HashMap<>();
            myMap.put(CommonAuthorizationConfig.ENABLED_PROPERTY_KEY, true);
            myMap.put(CommonAuthorizationConfig.TYPE_PROPERTY_KEY, authorizationType);
            myMap.put(CommonRbacConfig.RULES_FILE_KEY, rbacRulesFile);

            environment.getPropertySources().addLast(new MapPropertySource(CommonAuthorizationConfig.PROPERTIES_PREFIX, myMap));
        }
    }
}
