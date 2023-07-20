/*******************************************************************************
 * Copyright (C) 2023 DFKI GmbH (https://www.dfki.de/en/web)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelregistry.client.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.DataTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringDefinitionTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringPreferredNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LangStringShortNameTypeIec61360;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.LevelType;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ValueList;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * DataSpecificationIec61360
 */
@JsonPropertyOrder({
  DataSpecificationIec61360.JSON_PROPERTY_PREFERRED_NAME,
  DataSpecificationIec61360.JSON_PROPERTY_SHORT_NAME,
  DataSpecificationIec61360.JSON_PROPERTY_UNIT,
  DataSpecificationIec61360.JSON_PROPERTY_UNIT_ID,
  DataSpecificationIec61360.JSON_PROPERTY_SOURCE_OF_DEFINITION,
  DataSpecificationIec61360.JSON_PROPERTY_SYMBOL,
  DataSpecificationIec61360.JSON_PROPERTY_DATA_TYPE,
  DataSpecificationIec61360.JSON_PROPERTY_DEFINITION,
  DataSpecificationIec61360.JSON_PROPERTY_VALUE_FORMAT,
  DataSpecificationIec61360.JSON_PROPERTY_VALUE_LIST,
  DataSpecificationIec61360.JSON_PROPERTY_VALUE,
  DataSpecificationIec61360.JSON_PROPERTY_LEVEL_TYPE,
  DataSpecificationIec61360.JSON_PROPERTY_MODEL_TYPE
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-07-18T15:02:01.566475800+02:00[Europe/Berlin]")
public class DataSpecificationIec61360 {
  public static final String JSON_PROPERTY_PREFERRED_NAME = "preferredName";
  private List<LangStringPreferredNameTypeIec61360> preferredName = new ArrayList<>();

  public static final String JSON_PROPERTY_SHORT_NAME = "shortName";
  private List<LangStringShortNameTypeIec61360> shortName;

  public static final String JSON_PROPERTY_UNIT = "unit";
  private String unit;

  public static final String JSON_PROPERTY_UNIT_ID = "unitId";
  private Reference unitId;

  public static final String JSON_PROPERTY_SOURCE_OF_DEFINITION = "sourceOfDefinition";
  private String sourceOfDefinition;

  public static final String JSON_PROPERTY_SYMBOL = "symbol";
  private String symbol;

  public static final String JSON_PROPERTY_DATA_TYPE = "dataType";
  private DataTypeIec61360 dataType;

  public static final String JSON_PROPERTY_DEFINITION = "definition";
  private List<LangStringDefinitionTypeIec61360> definition;

  public static final String JSON_PROPERTY_VALUE_FORMAT = "valueFormat";
  private String valueFormat;

  public static final String JSON_PROPERTY_VALUE_LIST = "valueList";
  private ValueList valueList;

  public static final String JSON_PROPERTY_VALUE = "value";
  private String value;

  public static final String JSON_PROPERTY_LEVEL_TYPE = "levelType";
  private LevelType levelType;

  public static final String JSON_PROPERTY_MODEL_TYPE = "modelType";
  private String modelType = "DataSpecificationIec61360";

  public DataSpecificationIec61360() { 
  }

  public DataSpecificationIec61360 preferredName(List<LangStringPreferredNameTypeIec61360> preferredName) {
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
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_PREFERRED_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<LangStringPreferredNameTypeIec61360> getPreferredName() {
    return preferredName;
  }


  @JsonProperty(JSON_PROPERTY_PREFERRED_NAME)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setPreferredName(List<LangStringPreferredNameTypeIec61360> preferredName) {
    this.preferredName = preferredName;
  }


  public DataSpecificationIec61360 shortName(List<LangStringShortNameTypeIec61360> shortName) {
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SHORT_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<LangStringShortNameTypeIec61360> getShortName() {
    return shortName;
  }


  @JsonProperty(JSON_PROPERTY_SHORT_NAME)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setShortName(List<LangStringShortNameTypeIec61360> shortName) {
    this.shortName = shortName;
  }


  public DataSpecificationIec61360 unit(String unit) {
    this.unit = unit;
    return this;
  }

   /**
   * Get unit
   * @return unit
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_UNIT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getUnit() {
    return unit;
  }


  @JsonProperty(JSON_PROPERTY_UNIT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_UNIT_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Reference getUnitId() {
    return unitId;
  }


  @JsonProperty(JSON_PROPERTY_UNIT_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SOURCE_OF_DEFINITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSourceOfDefinition() {
    return sourceOfDefinition;
  }


  @JsonProperty(JSON_PROPERTY_SOURCE_OF_DEFINITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SYMBOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSymbol() {
    return symbol;
  }


  @JsonProperty(JSON_PROPERTY_SYMBOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_DATA_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public DataTypeIec61360 getDataType() {
    return dataType;
  }


  @JsonProperty(JSON_PROPERTY_DATA_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDataType(DataTypeIec61360 dataType) {
    this.dataType = dataType;
  }


  public DataSpecificationIec61360 definition(List<LangStringDefinitionTypeIec61360> definition) {
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_DEFINITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<LangStringDefinitionTypeIec61360> getDefinition() {
    return definition;
  }


  @JsonProperty(JSON_PROPERTY_DEFINITION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDefinition(List<LangStringDefinitionTypeIec61360> definition) {
    this.definition = definition;
  }


  public DataSpecificationIec61360 valueFormat(String valueFormat) {
    this.valueFormat = valueFormat;
    return this;
  }

   /**
   * Get valueFormat
   * @return valueFormat
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_VALUE_FORMAT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getValueFormat() {
    return valueFormat;
  }


  @JsonProperty(JSON_PROPERTY_VALUE_FORMAT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_VALUE_LIST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ValueList getValueList() {
    return valueList;
  }


  @JsonProperty(JSON_PROPERTY_VALUE_LIST)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getValue() {
    return value;
  }


  @JsonProperty(JSON_PROPERTY_VALUE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_LEVEL_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public LevelType getLevelType() {
    return levelType;
  }


  @JsonProperty(JSON_PROPERTY_LEVEL_TYPE)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_MODEL_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getModelType() {
    return modelType;
  }


  @JsonProperty(JSON_PROPERTY_MODEL_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setModelType(String modelType) {
    this.modelType = modelType;
  }


  /**
   * Return true if this DataSpecificationIec61360 object is equal to o.
   */
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

  /**
   * Convert the instance into URL query string.
   *
   * @return URL query string
   */
  public String toUrlQueryString() {
    return toUrlQueryString(null);
  }

  /**
   * Convert the instance into URL query string.
   *
   * @param prefix prefix of the query string
   * @return URL query string
   */
  public String toUrlQueryString(String prefix) {
    String suffix = "";
    String containerSuffix = "";
    String containerPrefix = "";
    if (prefix == null) {
      // style=form, explode=true, e.g. /pet?name=cat&type=manx
      prefix = "";
    } else {
      // deepObject style e.g. /pet?id[name]=cat&id[type]=manx
      prefix = prefix + "[";
      suffix = "]";
      containerSuffix = "]";
      containerPrefix = "[";
    }

    StringJoiner joiner = new StringJoiner("&");

    // add `preferredName` to the URL query string
    if (getPreferredName() != null) {
      for (int i = 0; i < getPreferredName().size(); i++) {
        if (getPreferredName().get(i) != null) {
          joiner.add(getPreferredName().get(i).toUrlQueryString(String.format("%spreferredName%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    // add `shortName` to the URL query string
    if (getShortName() != null) {
      for (int i = 0; i < getShortName().size(); i++) {
        if (getShortName().get(i) != null) {
          joiner.add(getShortName().get(i).toUrlQueryString(String.format("%sshortName%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    // add `unit` to the URL query string
    if (getUnit() != null) {
      joiner.add(String.format("%sunit%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getUnit()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `unitId` to the URL query string
    if (getUnitId() != null) {
      joiner.add(getUnitId().toUrlQueryString(prefix + "unitId" + suffix));
    }

    // add `sourceOfDefinition` to the URL query string
    if (getSourceOfDefinition() != null) {
      joiner.add(String.format("%ssourceOfDefinition%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getSourceOfDefinition()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `symbol` to the URL query string
    if (getSymbol() != null) {
      joiner.add(String.format("%ssymbol%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getSymbol()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `dataType` to the URL query string
    if (getDataType() != null) {
      joiner.add(String.format("%sdataType%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getDataType()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `definition` to the URL query string
    if (getDefinition() != null) {
      for (int i = 0; i < getDefinition().size(); i++) {
        if (getDefinition().get(i) != null) {
          joiner.add(getDefinition().get(i).toUrlQueryString(String.format("%sdefinition%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    // add `valueFormat` to the URL query string
    if (getValueFormat() != null) {
      joiner.add(String.format("%svalueFormat%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getValueFormat()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `valueList` to the URL query string
    if (getValueList() != null) {
      joiner.add(getValueList().toUrlQueryString(prefix + "valueList" + suffix));
    }

    // add `value` to the URL query string
    if (getValue() != null) {
      joiner.add(String.format("%svalue%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getValue()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `levelType` to the URL query string
    if (getLevelType() != null) {
      joiner.add(getLevelType().toUrlQueryString(prefix + "levelType" + suffix));
    }

    // add `modelType` to the URL query string
    if (getModelType() != null) {
      joiner.add(String.format("%smodelType%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getModelType()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    return joiner.toString();
  }
}

