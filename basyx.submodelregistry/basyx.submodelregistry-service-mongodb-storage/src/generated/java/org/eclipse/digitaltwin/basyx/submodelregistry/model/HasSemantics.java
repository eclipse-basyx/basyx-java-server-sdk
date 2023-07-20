package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * HasSemantics
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:02:35.777401700+02:00[Europe/Berlin]")
public class HasSemantics implements Serializable {

  private static final long serialVersionUID = 1L;

  private Reference semanticId;

  @Valid
  private List<@Valid Reference> supplementalSemanticIds;

  public HasSemantics semanticId(Reference semanticId) {
    this.semanticId = semanticId;
    return this;
  }

  /**
   * Get semanticId
   * @return semanticId
  */
  @Valid 
  @Schema(name = "semanticId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("semanticId")
  public Reference getSemanticId() {
    return semanticId;
  }

  public void setSemanticId(Reference semanticId) {
    this.semanticId = semanticId;
  }

  public HasSemantics supplementalSemanticIds(List<@Valid Reference> supplementalSemanticIds) {
    this.supplementalSemanticIds = supplementalSemanticIds;
    return this;
  }

  public HasSemantics addSupplementalSemanticIdsItem(Reference supplementalSemanticIdsItem) {
    if (this.supplementalSemanticIds == null) {
      this.supplementalSemanticIds = new ArrayList<>();
    }
    this.supplementalSemanticIds.add(supplementalSemanticIdsItem);
    return this;
  }

  /**
   * Get supplementalSemanticIds
   * @return supplementalSemanticIds
  */
  @Valid @Size(min = 1) 
  @Schema(name = "supplementalSemanticIds", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("supplementalSemanticIds")
  public List<@Valid Reference> getSupplementalSemanticIds() {
    return supplementalSemanticIds;
  }

  public void setSupplementalSemanticIds(List<@Valid Reference> supplementalSemanticIds) {
    this.supplementalSemanticIds = supplementalSemanticIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HasSemantics hasSemantics = (HasSemantics) o;
    return Objects.equals(this.semanticId, hasSemantics.semanticId) &&
        Objects.equals(this.supplementalSemanticIds, hasSemantics.supplementalSemanticIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(semanticId, supplementalSemanticIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class HasSemantics {\n");
    sb.append("    semanticId: ").append(toIndentedString(semanticId)).append("\n");
    sb.append("    supplementalSemanticIds: ").append(toIndentedString(supplementalSemanticIds)).append("\n");
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

