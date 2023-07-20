package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http.testconfig;

import org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.InMemoryConceptDescriptionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyConfig {

		@Bean
		public InMemoryConceptDescriptionRepository createConceptDescriptionRepository() {
			return new InMemoryConceptDescriptionRepository();
		}
}