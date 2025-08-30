package org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.confguration;

import org.eclipse.digitaltwin.basyx.aasdigitaltwinregistry.component.Delegate.DiscoveryEnhancedShellDescriptors;
import org.eclipse.digitaltwin.basyx.aasregistry.service.api.ShellDescriptorsApiDelegate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DigitalTwinRegistryConfig {

    @Bean
    @Primary
    public ShellDescriptorsApiDelegate discoveryEnhancedShellDescriptors(
            ObjectProvider<ShellDescriptorsApiDelegate> delegateProvider) {
        return new DiscoveryEnhancedShellDescriptors(delegateProvider);
    }
}
