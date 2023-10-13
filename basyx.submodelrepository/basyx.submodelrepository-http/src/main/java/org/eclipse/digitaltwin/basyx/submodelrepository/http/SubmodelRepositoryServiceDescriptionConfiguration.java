package org.eclipse.digitaltwin.basyx.submodelrepository.http;

import org.eclipse.digitaltwin.basyx.http.description.Profile;
import org.eclipse.digitaltwin.basyx.http.description.ProfileDeclaration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.TreeSet;

@Configuration
public class SubmodelRepositoryServiceDescriptionConfiguration {
  @Bean
  public ProfileDeclaration smRepositoryProfiles() {
    return () -> new TreeSet<>(List.of(Profile.SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_001,
        Profile.SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_002, Profile.SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_003,
        Profile.SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_004));
  }
}
