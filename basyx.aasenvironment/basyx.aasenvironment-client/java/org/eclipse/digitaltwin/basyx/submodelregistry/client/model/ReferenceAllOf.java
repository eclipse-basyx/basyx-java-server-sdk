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
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ReferenceParent;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * ReferenceAllOf
 */
@JsonPropertyOrder({
  ReferenceAllOf.JSON_PROPERTY_REFERRED_SEMANTIC_ID
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2024-11-11T08:29:25.882305+01:00[Europe/Berlin]")
public class ReferenceAllOf {
  public static final String JSON_PROPERTY_REFERRED_SEMANTIC_ID = "referredSemanticId";
  private ReferenceParent referredSemanticId;

  public ReferenceAllOf() { 
  }

  public ReferenceAllOf referredSemanticId(ReferenceParent referredSemanticId) {
    this.referredSemanticId = referredSemanticId;
    return this;
  }

   /**
   * Get referredSemanticId
   * @return referredSemanticId
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_REFERRED_SEMANTIC_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public ReferenceParent getReferredSemanticId() {
    return referredSemanticId;
  }


  @JsonProperty(JSON_PROPERTY_REFERRED_SEMANTIC_ID)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setReferredSemanticId(ReferenceParent referredSemanticId) {
    this.referredSemanticId = referredSemanticId;
  }


  /**
   * Return true if this Reference_allOf object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReferenceAllOf referenceAllOf = (ReferenceAllOf) o;
    return Objects.equals(this.referredSemanticId, referenceAllOf.referredSemanticId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(referredSemanticId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReferenceAllOf {\n");
    sb.append("    referredSemanticId: ").append(toIndentedString(referredSemanticId)).append("\n");
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

    // add `referredSemanticId` to the URL query string
    if (getReferredSemanticId() != null) {
      joiner.add(getReferredSemanticId().toUrlQueryString(prefix + "referredSemanticId" + suffix));
    }

    return joiner.toString();
  }
}

