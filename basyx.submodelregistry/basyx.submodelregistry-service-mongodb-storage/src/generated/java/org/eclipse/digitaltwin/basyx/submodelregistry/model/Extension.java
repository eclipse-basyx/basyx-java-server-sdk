package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Reference;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Extension
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:02:35.777401700+02:00[Europe/Berlin]")
public class Extension implements Serializable {

  private static final long serialVersionUID = 1L;

  private Reference semanticId;

  @Valid
  private List<@Valid Reference> supplementalSemanticIds;

  private String name;

  private DataTypeDefXsd valueType;

  private String value;

  @Valid
  private List<@Valid Reference> refersTo;

  /**
   * Default constructor
   * @deprecated Use {@link Extension#Extension(String)}
   */
  @Deprecated
  public Extension() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Extension(String name) {
    this.name = name;
  }

  public Extension semanticId(Reference semanticId) {
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

  public Extension supplementalSemanticIds(List<@Valid Reference> supplementalSemanticIds) {
    this.supplementalSemanticIds = supplementalSemanticIds;
    return this;
  }

  public Extension addSupplementalSemanticIdsItem(Reference supplementalSemanticIdsItem) {
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

  public Extension name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @NotNull @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1, max = 128) 
  @Schema(name = "name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Extension valueType(DataTypeDefXsd valueType) {
    this.valueType = valueType;
    return this;
  }

  /**
   * Get valueType
   * @return valueType
  */
  @Valid 
  @Schema(name = "valueType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valueType")
  public DataTypeDefXsd getValueType() {
    return valueType;
  }

  public void setValueType(DataTypeDefXsd valueType) {
    this.valueType = valueType;
  }

  public Extension value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  
  @Schema(name = "value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Extension refersTo(List<@Valid Reference> refersTo) {
    this.refersTo = refersTo;
    return this;
  }

  public Extension addRefersToItem(Reference refersToItem) {
    if (this.refersTo == null) {
      this.refersTo = new ArrayList<>();
    }
    this.refersTo.add(refersToItem);
    return this;
  }

  /**
   * Get refersTo
   * @return refersTo
  */
  @Valid @Size(min = 1) 
  @Schema(name = "refersTo", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("refersTo")
  public List<@Valid Reference> getRefersTo() {
    return refersTo;
  }

  public void setRefersTo(List<@Valid Reference> refersTo) {
    this.refersTo = refersTo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Extension extension = (Extension) o;
    return Objects.equals(this.semanticId, extension.semanticId) &&
        Objects.equals(this.supplementalSemanticIds, extension.supplementalSemanticIds) &&
        Objects.equals(this.name, extension.name) &&
        Objects.equals(this.valueType, extension.valueType) &&
        Objects.equals(this.value, extension.value) &&
        Objects.equals(this.refersTo, extension.refersTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(semanticId, supplementalSemanticIds, name, valueType, value, refersTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Extension {\n");
    sb.append("    semanticId: ").append(toIndentedString(semanticId)).append("\n");
    sb.append("    supplementalSemanticIds: ").append(toIndentedString(supplementalSemanticIds)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    valueType: ").append(toIndentedString(valueType)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    refersTo: ").append(toIndentedString(refersTo)).append("\n");
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

