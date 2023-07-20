package org.eclipse.digitaltwin.basyx.aasrepository.http.testconfig;

import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.InMemoryAasRepository;
import org.eclipse.digitaltwin.basyx.aasservice.backend.InMemoryAasServiceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyConfig {

	@Bean
    @ConditionalOnMissingBean
    public AasRepository createAasRepository() {
        return new InMemoryAasRepository(new InMemoryAasServiceFactory());
    }
}
