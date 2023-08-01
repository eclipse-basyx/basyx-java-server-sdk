package org.eclipse.digitaltwin.basyx.submodelregistry.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.digitaltwin.basyx.submodelregistry.model.ProtocolInformationSecurityAttributes;
import org.openapitools.jackson.nullable.JsonNullable;
import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ProtocolInformation
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-07-27T16:44:41.955927700+02:00[Europe/Berlin]")
public class ProtocolInformation implements Serializable {

  private static final long serialVersionUID = 1L;

  private String href;

  private String endpointProtocol;

  @Valid
  private List<String> endpointProtocolVersion;

  private String subprotocol;

  private String subprotocolBody;

  private String subprotocolBodyEncoding;

  @Valid
  private List<@Valid ProtocolInformationSecurityAttributes> securityAttributes;

  /**
   * Default constructor
   * @deprecated Use {@link ProtocolInformation#ProtocolInformation(String)}
   */
  @Deprecated
  public ProtocolInformation() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProtocolInformation(String href) {
    this.href = href;
  }

  public ProtocolInformation href(String href) {
    this.href = href;
    return this;
  }

  /**
   * Get href
   * @return href
  */
  @NotNull @Size(max = 2048) 
  @Schema(name = "href", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("href")
  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  public ProtocolInformation endpointProtocol(String endpointProtocol) {
    this.endpointProtocol = endpointProtocol;
    return this;
  }

  /**
   * Get endpointProtocol
   * @return endpointProtocol
  */
  @Size(max = 128) 
  @Schema(name = "endpointProtocol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endpointProtocol")
  public String getEndpointProtocol() {
    return endpointProtocol;
  }

  public void setEndpointProtocol(String endpointProtocol) {
    this.endpointProtocol = endpointProtocol;
  }

  public ProtocolInformation endpointProtocolVersion(List<String> endpointProtocolVersion) {
    this.endpointProtocolVersion = endpointProtocolVersion;
    return this;
  }

  public ProtocolInformation addEndpointProtocolVersionItem(String endpointProtocolVersionItem) {
    if (this.endpointProtocolVersion == null) {
      this.endpointProtocolVersion = new ArrayList<>();
    }
    this.endpointProtocolVersion.add(endpointProtocolVersionItem);
    return this;
  }

  /**
   * Get endpointProtocolVersion
   * @return endpointProtocolVersion
  */
  
  @Schema(name = "endpointProtocolVersion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("endpointProtocolVersion")
  public List<String> getEndpointProtocolVersion() {
    return endpointProtocolVersion;
  }

  public void setEndpointProtocolVersion(List<String> endpointProtocolVersion) {
    this.endpointProtocolVersion = endpointProtocolVersion;
  }

  public ProtocolInformation subprotocol(String subprotocol) {
    this.subprotocol = subprotocol;
    return this;
  }

  /**
   * Get subprotocol
   * @return subprotocol
  */
  @Size(max = 128) 
  @Schema(name = "subprotocol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subprotocol")
  public String getSubprotocol() {
    return subprotocol;
  }

  public void setSubprotocol(String subprotocol) {
    this.subprotocol = subprotocol;
  }

  public ProtocolInformation subprotocolBody(String subprotocolBody) {
    this.subprotocolBody = subprotocolBody;
    return this;
  }

  /**
   * Get subprotocolBody
   * @return subprotocolBody
  */
  @Size(max = 128) 
  @Schema(name = "subprotocolBody", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subprotocolBody")
  public String getSubprotocolBody() {
    return subprotocolBody;
  }

  public void setSubprotocolBody(String subprotocolBody) {
    this.subprotocolBody = subprotocolBody;
  }

  public ProtocolInformation subprotocolBodyEncoding(String subprotocolBodyEncoding) {
    this.subprotocolBodyEncoding = subprotocolBodyEncoding;
    return this;
  }

  /**
   * Get subprotocolBodyEncoding
   * @return subprotocolBodyEncoding
  */
  @Size(max = 128) 
  @Schema(name = "subprotocolBodyEncoding", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("subprotocolBodyEncoding")
  public String getSubprotocolBodyEncoding() {
    return subprotocolBodyEncoding;
  }

  public void setSubprotocolBodyEncoding(String subprotocolBodyEncoding) {
    this.subprotocolBodyEncoding = subprotocolBodyEncoding;
  }

  public ProtocolInformation securityAttributes(List<@Valid ProtocolInformationSecurityAttributes> securityAttributes) {
    this.securityAttributes = securityAttributes;
    return this;
  }

  public ProtocolInformation addSecurityAttributesItem(ProtocolInformationSecurityAttributes securityAttributesItem) {
    if (this.securityAttributes == null) {
      this.securityAttributes = new ArrayList<>();
    }
    this.securityAttributes.add(securityAttributesItem);
    return this;
  }

  /**
   * Get securityAttributes
   * @return securityAttributes
  */
  @Valid @Size(min = 1) 
  @Schema(name = "securityAttributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("securityAttributes")
  public List<@Valid ProtocolInformationSecurityAttributes> getSecurityAttributes() {
    return securityAttributes;
  }

  public void setSecurityAttributes(List<@Valid ProtocolInformationSecurityAttributes> securityAttributes) {
    this.securityAttributes = securityAttributes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ProtocolInformation protocolInformation = (ProtocolInformation) o;
    return Objects.equals(this.href, protocolInformation.href) &&
        Objects.equals(this.endpointProtocol, protocolInformation.endpointProtocol) &&
        Objects.equals(this.endpointProtocolVersion, protocolInformation.endpointProtocolVersion) &&
        Objects.equals(this.subprotocol, protocolInformation.subprotocol) &&
        Objects.equals(this.subprotocolBody, protocolInformation.subprotocolBody) &&
        Objects.equals(this.subprotocolBodyEncoding, protocolInformation.subprotocolBodyEncoding) &&
        Objects.equals(this.securityAttributes, protocolInformation.securityAttributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(href, endpointProtocol, endpointProtocolVersion, subprotocol, subprotocolBody, subprotocolBodyEncoding, securityAttributes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProtocolInformation {\n");
    sb.append("    href: ").append(toIndentedString(href)).append("\n");
    sb.append("    endpointProtocol: ").append(toIndentedString(endpointProtocol)).append("\n");
    sb.append("    endpointProtocolVersion: ").append(toIndentedString(endpointProtocolVersion)).append("\n");
    sb.append("    subprotocol: ").append(toIndentedString(subprotocol)).append("\n");
    sb.append("    subprotocolBody: ").append(toIndentedString(subprotocolBody)).append("\n");
    sb.append("    subprotocolBodyEncoding: ").append(toIndentedString(subprotocolBodyEncoding)).append("\n");
    sb.append("    securityAttributes: ").append(toIndentedString(securityAttributes)).append("\n");
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

