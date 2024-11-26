package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The Description object enables servers to present their capabilities to the clients, in particular which profiles they implement. At least one defined profile is required. Additional, proprietary attributes might be included. Nevertheless, the server must not expect that a regular client understands them.
 */

@Schema(name = "ServiceDescription", description = "The Description object enables servers to present their capabilities to the clients, in particular which profiles they implement. At least one defined profile is required. Additional, proprietary attributes might be included. Nevertheless, the server must not expect that a regular client understands them.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class ServiceDescription implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Gets or Sets profiles
   */
  public enum ProfilesEnum {
    ASSETADMINISTRATIONSHELLSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellServiceSpecification/SSP-001"),
    
    ASSETADMINISTRATIONSHELLSERVICESPECIFICATION_SSP_002("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellServiceSpecification/SSP-002"),
    
    SUBMODELSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-001"),
    
    SUBMODELSERVICESPECIFICATION_SSP_002("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-002"),
    
    SUBMODELSERVICESPECIFICATION_SSP_003("https://admin-shell.io/aas/API/3/0/SubmodelServiceSpecification/SSP-003"),
    
    AASXFILESERVERSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/AasxFileServerServiceSpecification/SSP-001"),
    
    ASSETADMINISTRATIONSHELLREGISTRYSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-001"),
    
    ASSETADMINISTRATIONSHELLREGISTRYSERVICESPECIFICATION_SSP_002("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRegistryServiceSpecification/SSP-002"),
    
    SUBMODELREGISTRYSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/SubmodelRegistryServiceSpecification/SSP-001"),
    
    SUBMODELREGISTRYSERVICESPECIFICATION_SSP_002("https://admin-shell.io/aas/API/3/0/SubmodelRegistryServiceSpecification/SSP-002"),
    
    DISCOVERYSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/DiscoveryServiceSpecification/SSP-001"),
    
    ASSETADMINISTRATIONSHELLREPOSITORYSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRepositoryServiceSpecification/SSP-001"),
    
    ASSETADMINISTRATIONSHELLREPOSITORYSERVICESPECIFICATION_SSP_002("https://admin-shell.io/aas/API/3/0/AssetAdministrationShellRepositoryServiceSpecification/SSP-002"),
    
    SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-001"),
    
    SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_002("https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-002"),
    
    SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_003("https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-003"),
    
    SUBMODELREPOSITORYSERVICESPECIFICATION_SSP_004("https://admin-shell.io/aas/API/3/0/SubmodelRepositoryServiceSpecification/SSP-004"),
    
    CONCEPTDESCRIPTIONSERVICESPECIFICATION_SSP_001("https://admin-shell.io/aas/API/3/0/ConceptDescriptionServiceSpecification/SSP-001");

    private String value;

    ProfilesEnum(String value) {
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
    public static ProfilesEnum fromValue(String value) {
      for (ProfilesEnum b : ProfilesEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @Valid
  private List<ProfilesEnum> profiles;

  public ServiceDescription profiles(List<ProfilesEnum> profiles) {
    this.profiles = profiles;
    return this;
  }

  public ServiceDescription addProfilesItem(ProfilesEnum profilesItem) {
    if (this.profiles == null) {
      this.profiles = new ArrayList<>();
    }
    this.profiles.add(profilesItem);
    return this;
  }

  /**
   * Get profiles
   * @return profiles
  */
  @Size(min = 1) 
  @Schema(name = "profiles", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profiles")
  public List<ProfilesEnum> getProfiles() {
    return profiles;
  }

  public void setProfiles(List<ProfilesEnum> profiles) {
    this.profiles = profiles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceDescription serviceDescription = (ServiceDescription) o;
    return Objects.equals(this.profiles, serviceDescription.profiles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(profiles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceDescription {\n");
    sb.append("    profiles: ").append(toIndentedString(profiles)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

