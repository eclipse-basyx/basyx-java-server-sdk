package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.eclipse.digitaltwin.basyx.aasregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SpecificAssetIdAllOf
 */

@JsonTypeName("SpecificAssetId_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class SpecificAssetIdAllOf implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;

  private String value;

  private Reference externalSubjectId;

  /**
   * Default constructor
   * @deprecated Use {@link SpecificAssetIdAllOf#SpecificAssetIdAllOf(String, String)}
   */
  @Deprecated
  public SpecificAssetIdAllOf() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SpecificAssetIdAllOf(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public SpecificAssetIdAllOf name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1, max = 64) 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SpecificAssetIdAllOf value(String value) {
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

  public SpecificAssetIdAllOf externalSubjectId(Reference externalSubjectId) {
    this.externalSubjectId = externalSubjectId;
    return this;
  }

  /**
   * Get externalSubjectId
   * @return externalSubjectId
  */
  @Valid 
  @Schema(name = "externalSubjectId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("externalSubjectId")
  public Reference getExternalSubjectId() {
    return externalSubjectId;
  }

  public void setExternalSubjectId(Reference externalSubjectId) {
    this.externalSubjectId = externalSubjectId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SpecificAssetIdAllOf specificAssetIdAllOf = (SpecificAssetIdAllOf) o;
    return Objects.equals(this.name, specificAssetIdAllOf.name) &&
        Objects.equals(this.value, specificAssetIdAllOf.value) &&
        Objects.equals(this.externalSubjectId, specificAssetIdAllOf.externalSubjectId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value, externalSubjectId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SpecificAssetIdAllOf {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    externalSubjectId: ").append(toIndentedString(externalSubjectId)).append("\n");
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

