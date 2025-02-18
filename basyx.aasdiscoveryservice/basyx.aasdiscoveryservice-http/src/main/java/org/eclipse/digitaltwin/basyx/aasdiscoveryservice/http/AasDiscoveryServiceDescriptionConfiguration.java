package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.http;

import java.util.List;
import java.util.TreeSet;

import org.eclipse.digitaltwin.basyx.http.description.Profile;
import org.eclipse.digitaltwin.basyx.http.description.ProfileDeclaration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AasDiscoveryServiceDescriptionConfiguration {
    @Bean
    public ProfileDeclaration aasDiscoveryProfiles() {
        return () -> new TreeSet<>(List.of(Profile.DISCOVERYSERVICESPECIFICATION_SSP_001));
    }
}