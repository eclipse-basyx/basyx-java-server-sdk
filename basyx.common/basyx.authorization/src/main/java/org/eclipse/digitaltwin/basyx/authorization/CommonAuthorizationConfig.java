package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class CommonAuthorizationConfig {
    public static final String PROPERTIES_PREFIX = "basyx.authorization";
    public static final String ENABLED_PROPERTY_KEY = PROPERTIES_PREFIX + ".enabled";
    public static final String TYPE_PROPERTY_KEY = PROPERTIES_PREFIX + ".type";
}
