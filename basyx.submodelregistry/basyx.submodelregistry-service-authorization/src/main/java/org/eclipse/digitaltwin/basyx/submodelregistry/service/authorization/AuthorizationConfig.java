package org.eclipse.digitaltwin.basyx.submodelregistry.service.authorization;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class AuthorizationConfig {
    public final static String ENABLED_PROPERTY_KEY = "basyx.submodelregistry.authorization.enabled";
    public final static String TYPE_PROPERTY_KEY = "basyx.submodelregistry.authorization.type";
}
