package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.DataTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.LevelType;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.ValueList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * DataSpecificationIec61360
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:01:40.984482400+02:00[Europe/Berlin]")
public class DataSpecificationIec61360 implements Serializable, DataSpecificationContent {

  private static final long serialVersionUID = 1L;

  @Valid
  private List<@Valid LangStringPreferredNameTypeIec61360> preferredName = new ArrayList<>();

  @Valid
  private List<@Valid LangStringShortNameTypeIec61360> shortName;

  private String unit;

  private Reference unitId;

  private String sourceOfDefinition;

  private String symbol;

  private DataTypeIec61360 dataType;

  @Valid
  private List<@Valid LangStringDefinitionTypeIec61360> definition;

  private String valueFormat;

  private ValueList valueList;

  private String value;

  private LevelType levelType;

  private String modelType = "DataSpecificationIec61360";

  /**
   * Default constructor
   * @deprecated Use {@link DataSpecificationIec61360#DataSpecificationIec61360(List<@Valid LangStringPreferredNameTypeIec61360>, String)}
   */
  @Deprecated
  public DataSpecificationIec61360() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DataSpecificationIec61360(List<@Valid LangStringPreferredNameTypeIec61360> preferredName, String modelType) {
    this.preferredName = preferredName;
    this.modelType = modelType;
  }

  public DataSpecificationIec61360 preferredName(List<@Valid LangStringPreferredNameTypeIec61360> preferredName) {
    this.preferredName = preferredName;
    return this;
  }

  public DataSpecificationIec61360 addPreferredNameItem(LangStringPreferredNameTypeIec61360 preferredNameItem) {
    if (this.preferredName == null) {
      this.preferredName = new ArrayList<>();
    }
    this.preferredName.add(preferredNameItem);
    return this;
  }

  /**
   * Get preferredName
   * @return preferredName
  */
  @NotNull @Valid @Size(min = 1) 
  @Schema(name = "preferredName", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("preferredName")
  public List<@Valid LangStringPreferredNameTypeIec61360> getPreferredName() {
    return preferredName;
  }

  public void setPreferredName(List<@Valid LangStringPreferredNameTypeIec61360> preferredName) {
    this.preferredName = preferredName;
  }

  public DataSpecificationIec61360 shortName(List<@Valid LangStringShortNameTypeIec61360> shortName) {
    this.shortName = shortName;
    return this;
  }

  public DataSpecificationIec61360 addShortNameItem(LangStringShortNameTypeIec61360 shortNameItem) {
    if (this.shortName == null) {
      this.shortName = new ArrayList<>();
    }
    this.shortName.add(shortNameItem);
    return this;
  }

  /**
   * Get shortName
   * @return shortName
  */
  @Valid @Size(min = 1) 
  @Schema(name = "shortName", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("shortName")
  public List<@Valid LangStringShortNameTypeIec61360> getShortName() {
    return shortName;
  }

  public void setShortName(List<@Valid LangStringShortNameTypeIec61360> shortName) {
    this.shortName = shortName;
  }

  public DataSpecificationIec61360 unit(String unit) {
    this.unit = unit;
    return this;
  }

  /**
   * Get unit
   * @return unit
  */
  @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1) 
  @Schema(name = "unit", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unit")
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public DataSpecificationIec61360 unitId(Reference unitId) {
    this.unitId = unitId;
    return this;
  }

  /**
   * Get unitId
   * @return unitId
  */
  @Valid 
  @Schema(name = "unitId", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("unitId")
  public Reference getUnitId() {
    return unitId;
  }

  public void setUnitId(Reference unitId) {
    this.unitId = unitId;
  }

  public DataSpecificationIec61360 sourceOfDefinition(String sourceOfDefinition) {
    this.sourceOfDefinition = sourceOfDefinition;
    return this;
  }

  /**
   * Get sourceOfDefinition
   * @return sourceOfDefinition
  */
  @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1) 
  @Schema(name = "sourceOfDefinition", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sourceOfDefinition")
  public String getSourceOfDefinition() {
    return sourceOfDefinition;
  }

  public void setSourceOfDefinition(String sourceOfDefinition) {
    this.sourceOfDefinition = sourceOfDefinition;
  }

  public DataSpecificationIec61360 symbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  /**
   * Get symbol
   * @return symbol
  */
  @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1) 
  @Schema(name = "symbol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("symbol")
  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public DataSpecificationIec61360 dataType(DataTypeIec61360 dataType) {
    this.dataType = dataType;
    return this;
  }

  /**
   * Get dataType
   * @return dataType
  */
  @Valid 
  @Schema(name = "dataType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("dataType")
  public DataTypeIec61360 getDataType() {
    return dataType;
  }

  public void setDataType(DataTypeIec61360 dataType) {
    this.dataType = dataType;
  }

  public DataSpecificationIec61360 definition(List<@Valid LangStringDefinitionTypeIec61360> definition) {
    this.definition = definition;
    return this;
  }

  public DataSpecificationIec61360 addDefinitionItem(LangStringDefinitionTypeIec61360 definitionItem) {
    if (this.definition == null) {
      this.definition = new ArrayList<>();
    }
    this.definition.add(definitionItem);
    return this;
  }

  /**
   * Get definition
   * @return definition
  */
  @Valid @Size(min = 1) 
  @Schema(name = "definition", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("definition")
  public List<@Valid LangStringDefinitionTypeIec61360> getDefinition() {
    return definition;
  }

  public void setDefinition(List<@Valid LangStringDefinitionTypeIec61360> definition) {
    this.definition = definition;
  }

  public DataSpecificationIec61360 valueFormat(String valueFormat) {
    this.valueFormat = valueFormat;
    return this;
  }

  /**
   * Get valueFormat
   * @return valueFormat
  */
  @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1) 
  @Schema(name = "valueFormat", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valueFormat")
  public String getValueFormat() {
    return valueFormat;
  }

  public void setValueFormat(String valueFormat) {
    this.valueFormat = valueFormat;
  }

  public DataSpecificationIec61360 valueList(ValueList valueList) {
    this.valueList = valueList;
    return this;
  }

  /**
   * Get valueList
   * @return valueList
  */
  @Valid 
  @Schema(name = "valueList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("valueList")
  public ValueList getValueList() {
    return valueList;
  }

  public void setValueList(ValueList valueList) {
    this.valueList = valueList;
  }

  public DataSpecificationIec61360 value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  @Pattern(regexp = "^([\\t\\n\\r -\\ud7ff\\ue000-\\ufffd]|\\ud800[\\udc00-\\udfff]|[\\ud801-\\udbfe][\\udc00-\\udfff]|\\udbff[\\udc00-\\udfff])*$") @Size(min = 1, max = 2000) 
  @Schema(name = "value", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public DataSpecificationIec61360 levelType(LevelType levelType) {
    this.levelType = levelType;
    return this;
  }

  /**
   * Get levelType
   * @return levelType
  */
  @Valid 
  @Schema(name = "levelType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("levelType")
  public LevelType getLevelType() {
    return levelType;
  }

  public void setLevelType(LevelType levelType) {
    this.levelType = levelType;
  }

  public DataSpecificationIec61360 modelType(String modelType) {
    this.modelType = modelType;
    return this;
  }

  /**
   * Get modelType
   * @return modelType
  */
  @NotNull 
  @Schema(name = "modelType", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelType")
  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataSpecificationIec61360 dataSpecificationIec61360 = (DataSpecificationIec61360) o;
    return Objects.equals(this.preferredName, dataSpecificationIec61360.preferredName) &&
        Objects.equals(this.shortName, dataSpecificationIec61360.shortName) &&
        Objects.equals(this.unit, dataSpecificationIec61360.unit) &&
        Objects.equals(this.unitId, dataSpecificationIec61360.unitId) &&
        Objects.equals(this.sourceOfDefinition, dataSpecificationIec61360.sourceOfDefinition) &&
        Objects.equals(this.symbol, dataSpecificationIec61360.symbol) &&
        Objects.equals(this.dataType, dataSpecificationIec61360.dataType) &&
        Objects.equals(this.definition, dataSpecificationIec61360.definition) &&
        Objects.equals(this.valueFormat, dataSpecificationIec61360.valueFormat) &&
        Objects.equals(this.valueList, dataSpecificationIec61360.valueList) &&
        Objects.equals(this.value, dataSpecificationIec61360.value) &&
        Objects.equals(this.levelType, dataSpecificationIec61360.levelType) &&
        Objects.equals(this.modelType, dataSpecificationIec61360.modelType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(preferredName, shortName, unit, unitId, sourceOfDefinition, symbol, dataType, definition, valueFormat, valueList, value, levelType, modelType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataSpecificationIec61360 {\n");
    sb.append("    preferredName: ").append(toIndentedString(preferredName)).append("\n");
    sb.append("    shortName: ").append(toIndentedString(shortName)).append("\n");
    sb.append("    unit: ").append(toIndentedString(unit)).append("\n");
    sb.append("    unitId: ").append(toIndentedString(unitId)).append("\n");
    sb.append("    sourceOfDefinition: ").append(toIndentedString(sourceOfDefinition)).append("\n");
    sb.append("    symbol: ").append(toIndentedString(symbol)).append("\n");
    sb.append("    dataType: ").append(toIndentedString(dataType)).append("\n");
    sb.append("    definition: ").append(toIndentedString(definition)).append("\n");
    sb.append("    valueFormat: ").append(toIndentedString(valueFormat)).append("\n");
    sb.append("    valueList: ").append(toIndentedString(valueList)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    levelType: ").append(toIndentedString(levelType)).append("\n");
    sb.append("    modelType: ").append(toIndentedString(modelType)).append("\n");
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

