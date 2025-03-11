package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.CrudAasDiscovery;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.CrudAasDiscoveryFactory;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ConditionalOnProperty(name="basyx.backend",havingValue = "InMemory")
@EnableJpaRepositories(basePackages = "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend")
@EntityScan(basePackages = "org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend")
@Import(CrudAasDiscoveryFactory.class)
public class H2AasDiscoveryDocumentBackendConfiguration {
    private static final Logger log = LoggerFactory.getLogger(H2AasDiscoveryDocumentBackendConfiguration.class);

}
