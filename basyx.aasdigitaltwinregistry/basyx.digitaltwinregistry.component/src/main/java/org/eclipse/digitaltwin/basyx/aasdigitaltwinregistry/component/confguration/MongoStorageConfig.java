package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.confguration;

import lombok.RequiredArgsConstructor;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBCrudAasDiscovery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("MongoDB")
public class MongoStorageConfig {

    @Bean
    public MongoDBCrudAasDiscovery mongoDBCrudAasDiscoveryBean(AasDiscoveryDocumentBackend aasDiscoveryDocumentBackend) {
        return new MongoDBCrudAasDiscovery(aasDiscoveryDocumentBackend, "Discovery");
    }
}
