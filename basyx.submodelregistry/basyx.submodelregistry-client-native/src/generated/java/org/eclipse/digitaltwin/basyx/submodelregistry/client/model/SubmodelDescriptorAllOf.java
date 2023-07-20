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
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.AdministrativeInformation;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Endpoint;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Reference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * SubmodelDescriptorAllOf
 */
@JsonPropertyOrder({
  SubmodelDescriptorAllOf.JSON_PROPERTY_ADMINISTRATION,
  SubmodelDescriptorAllOf.JSON_PROPERTY_ID_SHORT,
  SubmodelDescriptorAllOf.JSON_PROPERTY_ID,
  SubmodelDescriptorAllOf.JSON_PROPERTY_SEMANTIC_ID,
  SubmodelDescriptorAllOf.JSON_PROPERTY_SUPPLEMENTAL_SEMANTIC_ID,
  SubmodelDescriptorAllOf.JSON_PROPERTY_ENDPOINTS
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-07-18T15:02:01.566475800+02:00[Europe/Berlin]")
public class SubmodelDescriptorAllOf {
  public static final String JSON_PROPERTY_ADMINISTRATION = "administration";
  private AdministrativeInformation administration;

  public static final String JSON_PROPERTY_ID_SHORT = "idShort";
  private String idShort;

  public static final String JSON_PROPERTY_ID = "id";
  private String id;

  public static final String JSON_PROPERTY_SEMANTIC_ID = "semanticId";
  private Reference semanticId;

  public static final String JSON_PROPERTY_SUPPLEMENTAL_SEMANTIC_ID = "supplementalSemanticId";
  private List<Reference> supplementalSemanticId;

  public static final String JSON_PROPERTY_ENDPOINTS = "endpoints";
  private List<Endpoint> endpoints;

  public SubmodelDescriptorAllOf() { 
  }

  public SubmodelDescriptorAllOf administration(AdministrativeInformation administration) {
    this.administration = administration;
    return this;
  }

   /**
   * Get administration
   * @return administration
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ADMINISTRATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public AdministrativeInformation getAdministration() {
    return administration;
  }


  @JsonProperty(JSON_PROPERTY_ADMINISTRATION)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setAdministration(AdministrativeInformation administration) {
    this.administration = administration;
  }


  public SubmodelDescriptorAllOf idShort(String idShort) {
    this.idShort = idShort;
    return this;
  }

   /**
   * Get idShort
   * @return idShort
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ID_SHORT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getIdShort() {
    return idShort;
  }


  @JsonProperty(JSON_PROPERTY_ID_SHORT)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setIdShort(String idShort) {
    this.idShort = idShort;
  }


  public SubmodelDescriptorAllOf id(String id) {
    this.id = id;
    return this;
  }

   /**
   * Get id
   * @return id
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public String getId() {
    return id;
  }


  @JsonProperty(JSON_PROPERTY_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setId(String id) {
    this.id = id;
  }


  public SubmodelDescriptorAllOf semanticId(Reference semanticId) {
    this.semanticId = semanticId;
    return this;
  }

   /**
   * Get semanticId
   * @return semanticId
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SEMANTIC_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public Reference getSemanticId() {
    return semanticId;
  }


  @JsonProperty(JSON_PROPERTY_SEMANTIC_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSemanticId(Reference semanticId) {
    this.semanticId = semanticId;
  }


  public SubmodelDescriptorAllOf supplementalSemanticId(List<Reference> supplementalSemanticId) {
    this.supplementalSemanticId = supplementalSemanticId;
    return this;
  }

  public SubmodelDescriptorAllOf addSupplementalSemanticIdItem(Reference supplementalSemanticIdItem) {
    if (this.supplementalSemanticId == null) {
      this.supplementalSemanticId = new ArrayList<>();
    }
    this.supplementalSemanticId.add(supplementalSemanticIdItem);
    return this;
  }

   /**
   * Get supplementalSemanticId
   * @return supplementalSemanticId
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_SUPPLEMENTAL_SEMANTIC_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Reference> getSupplementalSemanticId() {
    return supplementalSemanticId;
  }


  @JsonProperty(JSON_PROPERTY_SUPPLEMENTAL_SEMANTIC_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setSupplementalSemanticId(List<Reference> supplementalSemanticId) {
    this.supplementalSemanticId = supplementalSemanticId;
  }


  public SubmodelDescriptorAllOf endpoints(List<Endpoint> endpoints) {
    this.endpoints = endpoints;
    return this;
  }

  public SubmodelDescriptorAllOf addEndpointsItem(Endpoint endpointsItem) {
    if (this.endpoints == null) {
      this.endpoints = new ArrayList<>();
    }
    this.endpoints.add(endpointsItem);
    return this;
  }

   /**
   * Get endpoints
   * @return endpoints
  **/
  @javax.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_ENDPOINTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public List<Endpoint> getEndpoints() {
    return endpoints;
  }


  @JsonProperty(JSON_PROPERTY_ENDPOINTS)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setEndpoints(List<Endpoint> endpoints) {
    this.endpoints = endpoints;
  }


  /**
   * Return true if this SubmodelDescriptor_allOf object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SubmodelDescriptorAllOf submodelDescriptorAllOf = (SubmodelDescriptorAllOf) o;
    return Objects.equals(this.administration, submodelDescriptorAllOf.administration) &&
        Objects.equals(this.idShort, submodelDescriptorAllOf.idShort) &&
        Objects.equals(this.id, submodelDescriptorAllOf.id) &&
        Objects.equals(this.semanticId, submodelDescriptorAllOf.semanticId) &&
        Objects.equals(this.supplementalSemanticId, submodelDescriptorAllOf.supplementalSemanticId) &&
        Objects.equals(this.endpoints, submodelDescriptorAllOf.endpoints);
  }

  @Override
  public int hashCode() {
    return Objects.hash(administration, idShort, id, semanticId, supplementalSemanticId, endpoints);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SubmodelDescriptorAllOf {\n");
    sb.append("    administration: ").append(toIndentedString(administration)).append("\n");
    sb.append("    idShort: ").append(toIndentedString(idShort)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    semanticId: ").append(toIndentedString(semanticId)).append("\n");
    sb.append("    supplementalSemanticId: ").append(toIndentedString(supplementalSemanticId)).append("\n");
    sb.append("    endpoints: ").append(toIndentedString(endpoints)).append("\n");
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

    // add `administration` to the URL query string
    if (getAdministration() != null) {
      joiner.add(getAdministration().toUrlQueryString(prefix + "administration" + suffix));
    }

    // add `idShort` to the URL query string
    if (getIdShort() != null) {
      joiner.add(String.format("%sidShort%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getIdShort()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `id` to the URL query string
    if (getId() != null) {
      joiner.add(String.format("%sid%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getId()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `semanticId` to the URL query string
    if (getSemanticId() != null) {
      joiner.add(getSemanticId().toUrlQueryString(prefix + "semanticId" + suffix));
    }

    // add `supplementalSemanticId` to the URL query string
    if (getSupplementalSemanticId() != null) {
      for (int i = 0; i < getSupplementalSemanticId().size(); i++) {
        if (getSupplementalSemanticId().get(i) != null) {
          joiner.add(getSupplementalSemanticId().get(i).toUrlQueryString(String.format("%ssupplementalSemanticId%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    // add `endpoints` to the URL query string
    if (getEndpoints() != null) {
      for (int i = 0; i < getEndpoints().size(); i++) {
        if (getEndpoints().get(i) != null) {
          joiner.add(getEndpoints().get(i).toUrlQueryString(String.format("%sendpoints%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    return joiner.toString();
  }
}

