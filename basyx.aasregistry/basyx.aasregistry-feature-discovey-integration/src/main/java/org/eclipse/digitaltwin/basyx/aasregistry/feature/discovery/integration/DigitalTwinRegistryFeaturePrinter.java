package org.eclipse.digitaltwin.basyx.aasregistry.feature.discovery.integration;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.aasregistry.service.storage.AasRegistryStorageFeature;

import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DigitalTwinRegistryFeaturePrinter {

	public DigitalTwinRegistryFeaturePrinter(List<AasRegistryStorageFeature> features) {
		log.info("------------------ Digital Twin registry Features: ------------------ ");
		for (AasRegistryStorageFeature feature : features) {
			log.info("BaSyxFeature " + feature.getName() + " is enabled: " + feature.isEnabled());
		}
		log.info("----------------------------------------------------------------- ");
	}
}
