package org.eclipse.digitaltwin.basyx.http.description;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.eclipse.digitaltwin.basyx.core.StandardizedLiteralEnum;

/**
 * Gets or Sets profiles
 */
public enum Profile implements StandardizedLiteralEnum {
  ASSETADMINISTRATIONSHELLSERVICESPECIFICATION_SSP_001(
      "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellServiceSpecification/SSP-001"),

  ASSETADMINISTRATIONSHELLSERVICESPECIFICATION_SSP_002(
      "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellServiceSpecification/SSP-002"),

  SUBMODELSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-001"),

  SUBMODELSERVICESPECIFICATION_SSP_002("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-002"),

  SUBMODELSERVICESPECIFICATION_SSP_003("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-003"),

  AASXFILESERVERSERVICESPECIFICATION_SSP_001(
      "https://admin-shell.io/aas/API/3/0/AasxFileServerServiceSpecification/SSP-001"),

  ASSETADMINISTRATIONSHELLREGISTRYSERVICESPECIFICATION_SSP_001(
      "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-001"),

  ASSETADMINISTRATIONSHELLREGISTRYSERVICESPECIFICATION_SSP_002(
      "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-002"),

  SUBMODELREGISTRYSERVICESPECIFICATION_SSP_001(
      "https://admin-shell.io/aas/API/3/0/SubmodelRegistryServiceSpecification/SSP-001"),

  SUBMODELREGISTRYSERVICESPECIFICATION_SSP_002(
      "https://admin-shell.io/aas/API/3/0/SubmodelRegistryServiceSpecification/SSP-002"),

  DISCOVERYSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/DiscoveryServiceSpecification/SSP-001"),

  ASSETADMINISTRATIONSHELLREPOSITORYSERVICESPECIFICATION_SSP_001(
      "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRepositoryServiceSpecification/SSP-001"),

  ASSETADMINISTRATIONSHELLREPOSITORYSERVICESPECIFICATION_SSP_002(
      "https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRepositoryServiceSpecification/SSP-002"),

  SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_001(
      "https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-001"),

  SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_002(
      "https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-002"),

  SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_003(
      "https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-003"),

  SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_004(
      "https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-004"),

  CONCEPTDESCRIPTIONSERVICESPECIFICATION_SSP_001(
      "https://admin-shell.io/aas/API/3/0/ConceptDescriptionServiceSpecification/SSP-001");

  private String value;

  Profile(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Profile fromValue(String value) {
    for (Profile b : Profile.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}
