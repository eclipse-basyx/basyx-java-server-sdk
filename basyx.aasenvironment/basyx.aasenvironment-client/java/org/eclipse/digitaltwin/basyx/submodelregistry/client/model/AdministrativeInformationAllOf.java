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
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * AdministrativeInformationAllOf
 */
@JsonPropertyOrder({
  AdministrativeInformationAllOf.JSON_PROPERTY_VERSION,
  AdministrativeInformationAllOf.JSON_PROPERTY_REVISION,
  AdministrativeInformationAllOf.JSON_PROPERTY_CREATOR,
  AdministrativeInformationAllOf.JSON_PROPERTY_TEMPLATE_ID
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-11-11T08:29:25.882305+01:00[Europe/Berlin]")
public class AdministrativeInformationAllOf {
  public static final String JSON_PROPERTY_VERSION = "version";
  private String version;

  public static final String JSON_PROPERTY_REVISION = "revision";
  private String revision;

  public static final String JSON_PROPERTY_CREATOR = "creator";
  private Reference creator;

  public static final String JSON_PROPERTY_TEMPLATE_ID = "templateId";
  private String templateId;

  public AdministrativeInformationAllOf() { 
  }

  public AdministrativeInformationAllOf version(String version) {
    this.version = version;
    return this;
  }

   /**
   * Get version
   * @return version
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getVersion() {
    return version;
  }


  @JsonProperty(JSON_PROPERTY_VERSION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setVersion(String version) {
    this.version = version;
  }


  public AdministrativeInformationAllOf revision(String revision) {
    this.revision = revision;
    return this;
  }

   /**
   * Get revision
   * @return revision
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_REVISION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getRevision() {
    return revision;
  }


  @JsonProperty(JSON_PROPERTY_REVISION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setRevision(String revision) {
    this.revision = revision;
  }


  public AdministrativeInformationAllOf creator(Reference creator) {
    this.creator = creator;
    return this;
  }

   /**
   * Get creator
   * @return creator
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_CREATOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Reference getCreator() {
    return creator;
  }


  @JsonProperty(JSON_PROPERTY_CREATOR)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setCreator(Reference creator) {
    this.creator = creator;
  }


  public AdministrativeInformationAllOf templateId(String templateId) {
    this.templateId = templateId;
    return this;
  }

   /**
   * Get templateId
   * @return templateId
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_TEMPLATE_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getTemplateId() {
    return templateId;
  }


  @JsonProperty(JSON_PROPERTY_TEMPLATE_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }


  /**
   * Return true if this AdministrativeInformation_allOf object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdministrativeInformationAllOf administrativeInformationAllOf = (AdministrativeInformationAllOf) o;
    return Objects.equals(this.version, administrativeInformationAllOf.version) &&
        Objects.equals(this.revision, administrativeInformationAllOf.revision) &&
        Objects.equals(this.creator, administrativeInformationAllOf.creator) &&
        Objects.equals(this.templateId, administrativeInformationAllOf.templateId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(version, revision, creator, templateId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AdministrativeInformationAllOf {\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    revision: ").append(toIndentedString(revision)).append("\n");
    sb.append("    creator: ").append(toIndentedString(creator)).append("\n");
    sb.append("    templateId: ").append(toIndentedString(templateId)).append("\n");
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

    // add `version` to the URL query string
    if (getVersion() != null) {
      joiner.add(String.format("%sversion%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getVersion()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `revision` to the URL query string
    if (getRevision() != null) {
      joiner.add(String.format("%srevision%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getRevision()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `creator` to the URL query string
    if (getCreator() != null) {
      joiner.add(getCreator().toUrlQueryString(prefix + "creator" + suffix));
    }

    // add `templateId` to the URL query string
    if (getTemplateId() != null) {
      joiner.add(String.format("%stemplateId%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getTemplateId()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    return joiner.toString();
  }
}

