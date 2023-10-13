package org.eclipse.digitaltwin.basyx.conceptdescriptionrepository.http;

import org.eclipse.digitaltwin.basyx.http.description.Profile;
import org.eclipse.digitaltwin.basyx.http.description.ProfileDeclaration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.TreeSet;

@Configuration
public class ConceptDescriptionRepositoryServiceDescriptionConfiguration {
  @Bean
  public ProfileDeclaration cdRepositoryProfiles() {
    return () -> new TreeSet<>(List.of(Profile.CONCEPTDESCRIPTIONSERVICESPECIFICATION_SSP_001));
  }
}
