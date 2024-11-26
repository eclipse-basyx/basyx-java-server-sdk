package org.eclipse.digitaltwin.basyx.aasregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ShellDescriptorQuery
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T10:50:44.252500800+01:00[Europe/Berlin]")
public class ShellDescriptorQuery implements Serializable {

  private static final long serialVersionUID = 1L;

  private String path;

  private String value;

  private String extensionName;

  /**
   * Gets or Sets queryType
   */
  public enum QueryTypeEnum {
    MATCH("match"),
    
    REGEX("regex");

    private String value;

    QueryTypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static QueryTypeEnum fromValue(String value) {
      for (QueryTypeEnum b : QueryTypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private QueryTypeEnum queryType = QueryTypeEnum.MATCH;

  private ShellDescriptorQuery combinedWith;

  /**
   * Default constructor
   * @deprecated Use {@link ShellDescriptorQuery#ShellDescriptorQuery(String, String)}
   */
  @Deprecated
  public ShellDescriptorQuery() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ShellDescriptorQuery(String path, String value) {
    this.path = path;
    this.value = value;
  }

  public ShellDescriptorQuery path(String path) {
    this.path = path;
    return this;
  }

  /**
   * Get path
   * @return path
  */
  @NotNull 
  @Schema(name = "path", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("path")
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public ShellDescriptorQuery value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  @NotNull 
  @Schema(name = "value", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ShellDescriptorQuery extensionName(String extensionName) {
    this.extensionName = extensionName;
    return this;
  }

  /**
   * If this property is set, the query applies only to the extension of this name. In this case, the path must reference the value property of the extension object. 
   * @return extensionName
  */
  
  @Schema(name = "extensionName", description = "If this property is set, the query applies only to the extension of this name. In this case, the path must reference the value property of the extension object. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("extensionName")
  public String getExtensionName() {
    return extensionName;
  }

  public void setExtensionName(String extensionName) {
    this.extensionName = extensionName;
  }

  public ShellDescriptorQuery queryType(QueryTypeEnum queryType) {
    this.queryType = queryType;
    return this;
  }

  /**
   * Get queryType
   * @return queryType
  */
  
  @Schema(name = "queryType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("queryType")
  public QueryTypeEnum getQueryType() {
    return queryType;
  }

  public void setQueryType(QueryTypeEnum queryType) {
    this.queryType = queryType;
  }

  public ShellDescriptorQuery combinedWith(ShellDescriptorQuery combinedWith) {
    this.combinedWith = combinedWith;
    return this;
  }

  /**
   * Get combinedWith
   * @return combinedWith
  */
  @Valid 
  @Schema(name = "combinedWith", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("combinedWith")
  public ShellDescriptorQuery getCombinedWith() {
    return combinedWith;
  }

  public void setCombinedWith(ShellDescriptorQuery combinedWith) {
    this.combinedWith = combinedWith;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShellDescriptorQuery shellDescriptorQuery = (ShellDescriptorQuery) o;
    return Objects.equals(this.path, shellDescriptorQuery.path) &&
        Objects.equals(this.value, shellDescriptorQuery.value) &&
        Objects.equals(this.extensionName, shellDescriptorQuery.extensionName) &&
        Objects.equals(this.queryType, shellDescriptorQuery.queryType) &&
        Objects.equals(this.combinedWith, shellDescriptorQuery.combinedWith);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, value, extensionName, queryType, combinedWith);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ShellDescriptorQuery {\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    extensionName: ").append(toIndentedString(extensionName)).append("\n");
    sb.append("    queryType: ").append(toIndentedString(queryType)).append("\n");
    sb.append("    combinedWith: ").append(toIndentedString(combinedWith)).append("\n");
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

