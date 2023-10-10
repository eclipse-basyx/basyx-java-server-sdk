package org.eclipse.digitaltwin.basyx.aasrepository.http;

import org.eclipse.digitaltwin.basyx.http.description.Profile;
import org.eclipse.digitaltwin.basyx.http.description.ProfileDeclaration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.TreeSet;

@Configuration
public class AasRepositoryServiceDescriptionConfiguration {
  @Bean
  public ProfileDeclaration aasRepositoryProfiles() {
    return () -> new TreeSet<>(List.of(Profile.ASSETADMINISTRATIONSHELLREPOSITORYSERVICESPECIFICATION_SSP_001,
        Profile.ASSETADMINISTRATIONSHELLREPOSITORYSERVICESPECIFICATION_SSP_002));
  }
}
