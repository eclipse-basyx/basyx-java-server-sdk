package org.eclipse.digitaltwin.basyx.aasregistry.feature.discovery.integration;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.digitaltwin.basyx.submodelrepository.feature.SubmodelRepositoryFeature;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class DigitalTwinRegistryFeaturePrinter {

	@Autowired
	public DigitalTwinRegistryFeaturePrinter(List<SubmodelRepositoryFeature> features) {
		log.info("-------------------- Digital Twin Registry Features: --------------------");
		for (SubmodelRepositoryFeature feature : features) {
			log.info("BaSyxFeature " + feature.getName() + " is enabled: " + feature.isEnabled());
		}

		log.info("----------------------------------------------------------------- ");
	}
}
