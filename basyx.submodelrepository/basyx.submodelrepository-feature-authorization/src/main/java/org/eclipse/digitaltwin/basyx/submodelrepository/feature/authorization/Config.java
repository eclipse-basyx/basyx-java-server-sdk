package org.eclipse.digitaltwin.basyx.submodelrepository.feature.authorization;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:authorization.properties")
public class Config {
    private final Environment environment;
    public Config(Environment environment) {
        this.environment = environment;
    }
}
