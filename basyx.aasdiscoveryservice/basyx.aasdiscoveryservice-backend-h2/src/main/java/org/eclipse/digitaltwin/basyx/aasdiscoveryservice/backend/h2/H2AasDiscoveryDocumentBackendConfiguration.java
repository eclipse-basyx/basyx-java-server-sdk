package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.h2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
@EnableJpaRepositories(basePackages = "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend")
@EntityScan(basePackages = "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend")
public class H2AasDiscoveryDocumentBackendConfiguration {

}
