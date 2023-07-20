package org.eclipse.digitaltwin.basyx.submodelrepository.http.testconfig;

import org.eclipse.digitaltwin.basyx.submodelrepository.InMemorySubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelservice.InMemorySubmodelServiceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyConfig {

@Bean
public InMemorySubmodelRepository createSubmodelRepository() {
	return new InMemorySubmodelRepository(new InMemorySubmodelServiceFactory());
}
}