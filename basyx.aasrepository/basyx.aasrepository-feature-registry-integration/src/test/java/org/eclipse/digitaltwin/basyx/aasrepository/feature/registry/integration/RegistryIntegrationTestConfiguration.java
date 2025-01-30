package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositoryFactory;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.AasRepositoryFeature;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.DecoratedAasRepositoryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryIntegrationTestConfiguration {
    @Bean
    AasRepository getAasRepository(AasRepositoryFactory aasRepositoryFactory, List<AasRepositoryFeature> features) {
        return new DecoratedAasRepositoryFactory(aasRepositoryFactory, features).create();
    }
}
