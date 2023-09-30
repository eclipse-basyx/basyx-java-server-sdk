package org.eclipse.digitaltwin.basyx.authorization;

import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Conditional;

@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@Conditional(DisableSpringSecurityIfNoAuthorizationConfig.NoAuthorizationCondition.class)
public class DisableSpringSecurityIfNoAuthorizationConfig {
    public static class NoAuthorizationCondition extends NoneNestedConditions {
        public NoAuthorizationCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnProperty(value = CommonAuthorizationConfig.ENABLED_PROPERTY_KEY, havingValue = "true")
        public static class AuthorizationCondition {
        }
    }
}
