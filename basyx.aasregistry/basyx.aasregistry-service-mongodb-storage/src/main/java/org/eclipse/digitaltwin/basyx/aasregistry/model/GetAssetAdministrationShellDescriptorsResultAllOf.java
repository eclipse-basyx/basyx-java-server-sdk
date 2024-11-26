package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.aasregistry.model.AssetAdministrationShellDescriptor;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * GetAssetAdministrationShellDescriptorsResultAllOf
 */

@JsonTypeName("GetAssetAdministrationShellDescriptorsResult_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class GetAssetAdministrationShellDescriptorsResultAllOf implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid AssetAdministrationShellDescriptor> result;

  public GetAssetAdministrationShellDescriptorsResultAllOf result(List<@Valid AssetAdministrationShellDescriptor> result) {
    this.result = result;
    return this;
  }

  public GetAssetAdministrationShellDescriptorsResultAllOf addResultItem(AssetAdministrationShellDescriptor resultItem) {
    if (this.result == null) {
      this.result = new ArrayList<>();
    }
    this.result.add(resultItem);
    return this;
  }

  /**
   * Get result
   * @return result
  */
  @Valid 
  @Schema(name = "result", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("result")
  public List<@Valid AssetAdministrationShellDescriptor> getResult() {
    return result;
  }

  public void setResult(List<@Valid AssetAdministrationShellDescriptor> result) {
    this.result = result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GetAssetAdministrationShellDescriptorsResultAllOf getAssetAdministrationShellDescriptorsResultAllOf = (GetAssetAdministrationShellDescriptorsResultAllOf) o;
    return Objects.equals(this.result, getAssetAdministrationShellDescriptorsResultAllOf.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GetAssetAdministrationShellDescriptorsResultAllOf {\n");
    sb.append("    result: ").append(toIndentedString(result)).append("\n");
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

