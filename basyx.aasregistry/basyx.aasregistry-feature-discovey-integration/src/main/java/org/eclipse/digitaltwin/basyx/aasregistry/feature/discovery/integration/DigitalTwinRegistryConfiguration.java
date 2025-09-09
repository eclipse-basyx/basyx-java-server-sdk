package org.eclipse.digitaltwin.basyx.aasregistry.feature.discovery.integration;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.AasDiscoveryDocumentBackend;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.mongodb.backend.MongoDBCrudAasDiscovery;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiDelegate;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
@ConditionalOnProperty(name = "digitaltwinregistry.enabled", havingValue = "true", matchIfMissing = false)
public class DigitalTwinRegistryConfiguration {

	@Bean
	@Primary
	public ShellDescriptorsApiDelegate discoveryEnhancedShellDescriptors(
			AasRegistryStorage storage,
			ObjectProvider<ShellDescriptorsApiDelegate> delegateProvider,
			MongoDBCrudAasDiscovery mongoDBCrudAasDiscovery) {
		log.info("DigitalTwinRegistryConfiguration loaded - digital twin registry is ENABLED");
		return new DiscoveryEnhancedShellDescriptors(storage, delegateProvider, mongoDBCrudAasDiscovery);
	}

	@Bean
	public MongoDBCrudAasDiscovery mongoDBCrudAasDiscoveryBean(AasDiscoveryDocumentBackend aasDiscoveryDocumentBackend) {
		return new MongoDBCrudAasDiscovery(aasDiscoveryDocumentBackend, "Discovery");
	}
}