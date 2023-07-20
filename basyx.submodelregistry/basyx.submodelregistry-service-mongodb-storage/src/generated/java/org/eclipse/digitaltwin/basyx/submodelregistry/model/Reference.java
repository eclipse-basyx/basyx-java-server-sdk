package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Key;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.ReferenceParent;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.ReferenceTypes;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Reference
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:02:35.777401700+02:00[Europe/Berlin]")
public class Reference implements Serializable {

  private static final long serialVersionUID = 1L;

  private ReferenceTypes type;

  @Valid
  private List<@Valid Key> keys = new ArrayList<>();

  private ReferenceParent referredSemanticId;

  /**
   * Default constructor
   * @deprecated Use {@link Reference#Reference(ReferenceTypes, List<@Valid Key>)}
   */
  @Deprecated
  public Reference() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Reference(ReferenceTypes type, List<@Valid Key> keys) {
    this.type = type;
    this.keys = keys;
  }

  public Reference type(ReferenceTypes type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @NotNull @Valid 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public ReferenceTypes getType() {
    return type;
  }

  public void setType(ReferenceTypes type) {
    this.type = type;
  }

  public Reference keys(List<@Valid Key> keys) {
    this.keys = keys;
    return this;
  }

  public Reference addKeysItem(Key keysItem) {
    if (this.keys == null) {
      this.keys = new ArrayList<>();
    }
    this.keys.add(keysItem);
    return this;
  }

  /**
   * Get keys
   * @return keys
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "keys", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("keys")
  public List<@Valid Key> getKeys() {
    return keys;
  }

  public void setKeys(List<@Valid Key> keys) {
    this.keys = keys;
  }

  public Reference referredSemanticId(ReferenceParent referredSemanticId) {
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
    Reference reference = (Reference) o;
    return Objects.equals(this.type, reference.type) &&
        Objects.equals(this.keys, reference.keys) &&
        Objects.equals(this.referredSemanticId, reference.referredSemanticId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, keys, referredSemanticId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Reference {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
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

