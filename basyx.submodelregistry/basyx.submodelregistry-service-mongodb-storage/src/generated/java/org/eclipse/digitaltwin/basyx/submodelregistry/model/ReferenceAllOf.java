package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.ReferenceParent;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReferenceAllOf
 */

@JsonTypeName("Reference_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:46:10.102240900+02:00[Europe/Berlin]")
public class ReferenceAllOf implements Serializable {

  private static final long serialVersionUID = 1L;

  private ReferenceParent referredSemanticId;

  public ReferenceAllOf referredSemanticId(ReferenceParent referredSemanticId) {
    this.referredSemanticId = referredSemanticId;
    return this;
  }

  /**
   * Get referredSemanticId
   * @return referredSemanticId
  */
  @Valid 
  @Schema(name = "referredSemanticId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("referredSemanticId")
  public ReferenceParent getReferredSemanticId() {
    return referredSemanticId;
  }

  public void setReferredSemanticId(ReferenceParent referredSemanticId) {
    this.referredSemanticId = referredSemanticId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferenceAllOf referenceAllOf = (ReferenceAllOf) o;
    return Objects.equals(this.referredSemanticId, referenceAllOf.referredSemanticId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(referredSemanticId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReferenceAllOf {\n");
    sb.append("    referredSemanticId: ").append(toIndentedString(referredSemanticId)).append("\n");
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

