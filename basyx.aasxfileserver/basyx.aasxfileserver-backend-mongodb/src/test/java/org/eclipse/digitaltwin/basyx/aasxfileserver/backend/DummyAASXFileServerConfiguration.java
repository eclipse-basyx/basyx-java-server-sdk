package org.eclipse.digitaltwin.basyx.aasxfileserver.backend;

import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServer;
import org.eclipse.digitaltwin.basyx.aasxfileserver.AASXFileServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyAASXFileServerConfiguration {

    @Bean
    AASXFileServer aasxFileServer(AASXFileServerFactory factory) {
        return factory.create();
    }

}
