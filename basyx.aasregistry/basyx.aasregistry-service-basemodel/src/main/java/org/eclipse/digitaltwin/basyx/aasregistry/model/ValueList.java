package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.aasregistry.model.ValueReferencePair;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ValueList
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:20.873153600+01:00[Europe/Berlin]")
public class ValueList implements Serializable {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid ValueReferencePair> valueReferencePairs = new ArrayList<>();

  /**
   * Default constructor
   * @deprecated Use {@link ValueList#ValueList(List<@Valid ValueReferencePair>)}
   */
  @Deprecated
  public ValueList() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ValueList(List<@Valid ValueReferencePair> valueReferencePairs) {
    this.valueReferencePairs = valueReferencePairs;
  }

  public ValueList valueReferencePairs(List<@Valid ValueReferencePair> valueReferencePairs) {
    this.valueReferencePairs = valueReferencePairs;
    return this;
  }

  public ValueList addValueReferencePairsItem(ValueReferencePair valueReferencePairsItem) {
    if (this.valueReferencePairs == null) {
      this.valueReferencePairs = new ArrayList<>();
    }
    this.valueReferencePairs.add(valueReferencePairsItem);
    return this;
  }

  /**
   * Get valueReferencePairs
   * @return valueReferencePairs
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "valueReferencePairs", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("valueReferencePairs")
  public List<@Valid ValueReferencePair> getValueReferencePairs() {
    return valueReferencePairs;
  }

  public void setValueReferencePairs(List<@Valid ValueReferencePair> valueReferencePairs) {
    this.valueReferencePairs = valueReferencePairs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValueList valueList = (ValueList) o;
    return Objects.equals(this.valueReferencePairs, valueList.valueReferencePairs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(valueReferencePairs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValueList {\n");
    sb.append("    valueReferencePairs: ").append(toIndentedString(valueReferencePairs)).append("\n");
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

