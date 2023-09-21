package org.eclipse.digitaltwin.basyx.conceptdescription.feature.authorization;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:authorization.properties")
@ComponentScan
public class Config {
}
