package org.eclipse.digitaltwin.basyx.aasxfileserver.http;

import java.util.List;
import java.util.TreeSet;

import org.eclipse.digitaltwin.basyx.http.description.Profile;
import org.eclipse.digitaltwin.basyx.http.description.ProfileDeclaration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AASXFileServerDescriptionConfiguration {
    @Bean
    public ProfileDeclaration aasxFileServerProfiles() {
        return () -> new TreeSet<>(List.of(Profile.AASXFILESERVERSERVICESPECIFICATION_SSP_001));
    }
}