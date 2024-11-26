package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ValueReferencePair
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class ValueReferencePair implements Serializable {

  private static final long serialVersionUID = 1L;

  private String value;

  private Reference valueId;

  /**
   * Default constructor
   * @deprecated Use {@link ValueReferencePair#ValueReferencePair(String, Reference)}
   */
  @Deprecated
  public ValueReferencePair() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ValueReferencePair(String value, Reference valueId) {
    this.value = value;
    this.valueId = valueId;
  }

  public ValueReferencePair value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  @NotNull @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1, max = 2000) 
  @Schema(name = "value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ValueReferencePair valueId(Reference valueId) {
    this.valueId = valueId;
    return this;
  }

  /**
   * Get valueId
   * @return valueId
  */
  @NotNull @Valid 
  @Schema(name = "valueId", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("valueId")
  public Reference getValueId() {
    return valueId;
  }

  public void setValueId(Reference valueId) {
    this.valueId = valueId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValueReferencePair valueReferencePair = (ValueReferencePair) o;
    return Objects.equals(this.value, valueReferencePair.value) &&
        Objects.equals(this.valueId, valueReferencePair.valueId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, valueId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValueReferencePair {\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    valueId: ").append(toIndentedString(valueId)).append("\n");
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

