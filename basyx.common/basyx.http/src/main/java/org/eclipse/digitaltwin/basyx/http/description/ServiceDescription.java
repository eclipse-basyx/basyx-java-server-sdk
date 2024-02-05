package org.eclipse.digitaltwin.basyx.http.description;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The Description object enables servers to present their capabilities to the clients, in particular which profiles
 * they implement. At least one defined profile is required. Additional, proprietary attributes might be included.
 * Nevertheless, the server must not expect that a regular client understands them.
 */

@Schema(name = "ServiceDescription",
  description = "The Description object enables servers to present their capabilities to the clients, in particular which profiles they implement. At least one defined profile is required. Additional, proprietary attributes might be included. Nevertheless, the server must not expect that a regular client understands them.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen",
  date = "2023-09-29T10:10:24.413141+02:00[Europe/Berlin]")
public class ServiceDescription implements Serializable {

  private static final long serialVersionUID = 1L;


  @Valid
  private List<Profile> profiles;

  public ServiceDescription profiles(List<Profile> profiles) {
    this.profiles = profiles;
    return this;
  }

  public ServiceDescription addProfilesItem(Profile profilesItem) {
    if (this.profiles == null) {
      this.profiles = new ArrayList<>();
    }
    this.profiles.add(profilesItem);
    return this;
  }

  /**
   * Get profiles
   *
   * @return profiles
   */
  @Size(min = 1)
  @Schema(name = "profiles", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("profiles")
  public List<Profile> getProfiles() {
    return profiles;
  }

  public void setProfiles(List<Profile> profiles) {
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
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

