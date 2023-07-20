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
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ProtocolInformationSecurityAttributes;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ProtocolInformation
 */
@JsonPropertyOrder({
  ProtocolInformation.JSON_PROPERTY_HREF,
  ProtocolInformation.JSON_PROPERTY_ENDPOINT_PROTOCOL,
  ProtocolInformation.JSON_PROPERTY_ENDPOINT_PROTOCOL_VERSION,
  ProtocolInformation.JSON_PROPERTY_SUBPROTOCOL,
  ProtocolInformation.JSON_PROPERTY_SUBPROTOCOL_BODY,
  ProtocolInformation.JSON_PROPERTY_SUBPROTOCOL_BODY_ENCODING,
  ProtocolInformation.JSON_PROPERTY_SECURITY_ATTRIBUTES
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-07-18T15:02:01.566475800+02:00[Europe/Berlin]")
public class ProtocolInformation {
  public static final String JSON_PROPERTY_HREF = "href";
  private String href;

  public static final String JSON_PROPERTY_ENDPOINT_PROTOCOL = "endpointProtocol";
  private String endpointProtocol;

  public static final String JSON_PROPERTY_ENDPOINT_PROTOCOL_VERSION = "endpointProtocolVersion";
  private List<String> endpointProtocolVersion;

  public static final String JSON_PROPERTY_SUBPROTOCOL = "subprotocol";
  private String subprotocol;

  public static final String JSON_PROPERTY_SUBPROTOCOL_BODY = "subprotocolBody";
  private String subprotocolBody;

  public static final String JSON_PROPERTY_SUBPROTOCOL_BODY_ENCODING = "subprotocolBodyEncoding";
  private String subprotocolBodyEncoding;

  public static final String JSON_PROPERTY_SECURITY_ATTRIBUTES = "securityAttributes";
  private List<ProtocolInformationSecurityAttributes> securityAttributes;

  public ProtocolInformation() { 
  }

  public ProtocolInformation href(String href) {
    this.href = href;
    return this;
  }

   /**
   * Get href
   * @return href
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_HREF)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public String getHref() {
    return href;
  }


  @JsonProperty(JSON_PROPERTY_HREF)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ENDPOINT_PROTOCOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getEndpointProtocol() {
    return endpointProtocol;
  }


  @JsonProperty(JSON_PROPERTY_ENDPOINT_PROTOCOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ENDPOINT_PROTOCOL_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<String> getEndpointProtocolVersion() {
    return endpointProtocolVersion;
  }


  @JsonProperty(JSON_PROPERTY_ENDPOINT_PROTOCOL_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUBPROTOCOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSubprotocol() {
    return subprotocol;
  }


  @JsonProperty(JSON_PROPERTY_SUBPROTOCOL)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUBPROTOCOL_BODY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSubprotocolBody() {
    return subprotocolBody;
  }


  @JsonProperty(JSON_PROPERTY_SUBPROTOCOL_BODY)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUBPROTOCOL_BODY_ENCODING)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getSubprotocolBodyEncoding() {
    return subprotocolBodyEncoding;
  }


  @JsonProperty(JSON_PROPERTY_SUBPROTOCOL_BODY_ENCODING)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSubprotocolBodyEncoding(String subprotocolBodyEncoding) {
    this.subprotocolBodyEncoding = subprotocolBodyEncoding;
  }


  public ProtocolInformation securityAttributes(List<ProtocolInformationSecurityAttributes> securityAttributes) {
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
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SECURITY_ATTRIBUTES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<ProtocolInformationSecurityAttributes> getSecurityAttributes() {
    return securityAttributes;
  }


  @JsonProperty(JSON_PROPERTY_SECURITY_ATTRIBUTES)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSecurityAttributes(List<ProtocolInformationSecurityAttributes> securityAttributes) {
    this.securityAttributes = securityAttributes;
  }


  /**
   * Return true if this ProtocolInformation object is equal to o.
   */
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

    // add `href` to the URL query string
    if (getHref() != null) {
      joiner.add(String.format("%shref%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getHref()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `endpointProtocol` to the URL query string
    if (getEndpointProtocol() != null) {
      joiner.add(String.format("%sendpointProtocol%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getEndpointProtocol()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `endpointProtocolVersion` to the URL query string
    if (getEndpointProtocolVersion() != null) {
      for (int i = 0; i < getEndpointProtocolVersion().size(); i++) {
        joiner.add(String.format("%sendpointProtocolVersion%s%s=%s", prefix, suffix,
            "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix),
            URLEncoder.encode(String.valueOf(getEndpointProtocolVersion().get(i)), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
      }
    }

    // add `subprotocol` to the URL query string
    if (getSubprotocol() != null) {
      joiner.add(String.format("%ssubprotocol%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getSubprotocol()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `subprotocolBody` to the URL query string
    if (getSubprotocolBody() != null) {
      joiner.add(String.format("%ssubprotocolBody%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getSubprotocolBody()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `subprotocolBodyEncoding` to the URL query string
    if (getSubprotocolBodyEncoding() != null) {
      joiner.add(String.format("%ssubprotocolBodyEncoding%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getSubprotocolBodyEncoding()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `securityAttributes` to the URL query string
    if (getSecurityAttributes() != null) {
      for (int i = 0; i < getSecurityAttributes().size(); i++) {
        if (getSecurityAttributes().get(i) != null) {
          joiner.add(getSecurityAttributes().get(i).toUrlQueryString(String.format("%ssecurityAttributes%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    return joiner.toString();
  }
}

