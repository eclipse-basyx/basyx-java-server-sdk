package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationConfig;
import org.eclipse.digitaltwin.basyx.authorization.rbac.CommonRbacConfig;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.AuthorizationConfig.ENABLED_PROPERTY_KEY;
import static org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.AuthorizationConfig.TYPE_PROPERTY_KEY;
import static org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization.rbac.RbacConfig.RULES_FILE_KEY;

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
