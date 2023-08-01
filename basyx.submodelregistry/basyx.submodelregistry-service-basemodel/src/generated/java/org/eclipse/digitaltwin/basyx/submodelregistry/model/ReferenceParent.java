package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Key;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.ReferenceTypes;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReferenceParent
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:44:41.955927700+02:00[Europe/Berlin]")
public class ReferenceParent implements Serializable {

  private static final long serialVersionUID = 1L;

  private ReferenceTypes type;

  @Valid
  private List<@Valid Key> keys = new ArrayList<>();

  /**
   * Default constructor
   * @deprecated Use {@link ReferenceParent#ReferenceParent(ReferenceTypes, List<@Valid Key>)}
   */
  @Deprecated
  public ReferenceParent() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ReferenceParent(ReferenceTypes type, List<@Valid Key> keys) {
    this.type = type;
    this.keys = keys;
  }

  public ReferenceParent type(ReferenceTypes type) {
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

  public ReferenceParent keys(List<@Valid Key> keys) {
    this.keys = keys;
    return this;
  }

  public ReferenceParent addKeysItem(Key keysItem) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferenceParent referenceParent = (ReferenceParent) o;
    return Objects.equals(this.type, referenceParent.type) &&
        Objects.equals(this.keys, referenceParent.keys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, keys);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReferenceParent {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
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

