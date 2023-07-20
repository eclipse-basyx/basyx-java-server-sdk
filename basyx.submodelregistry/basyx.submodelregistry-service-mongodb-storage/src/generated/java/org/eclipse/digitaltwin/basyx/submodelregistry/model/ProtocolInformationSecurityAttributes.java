package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ProtocolInformationSecurityAttributes
 */

@JsonTypeName("ProtocolInformation_securityAttributes")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-18T15:02:35.777401700+02:00[Europe/Berlin]")
public class ProtocolInformationSecurityAttributes implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    NONE("NONE"),
    
    RFC_TLSA("RFC_TLSA"),
    
    W3C_DID("W3C_DID");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  private String key;

  private String value;

  /**
   * Default constructor
   * @deprecated Use {@link ProtocolInformationSecurityAttributes#ProtocolInformationSecurityAttributes(TypeEnum, String, String)}
   */
  @Deprecated
  public ProtocolInformationSecurityAttributes() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProtocolInformationSecurityAttributes(TypeEnum type, String key, String value) {
    this.type = type;
    this.key = key;
    this.value = value;
  }

  public ProtocolInformationSecurityAttributes type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  */
  @NotNull 
  @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public ProtocolInformationSecurityAttributes key(String key) {
    this.key = key;
    return this;
  }

  /**
   * Get key
   * @return key
  */
  @NotNull 
  @Schema(name = "key", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("key")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public ProtocolInformationSecurityAttributes value(String value) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProtocolInformationSecurityAttributes protocolInformationSecurityAttributes = (ProtocolInformationSecurityAttributes) o;
    return Objects.equals(this.type, protocolInformationSecurityAttributes.type) &&
        Objects.equals(this.key, protocolInformationSecurityAttributes.key) &&
        Objects.equals(this.value, protocolInformationSecurityAttributes.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProtocolInformationSecurityAttributes {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

