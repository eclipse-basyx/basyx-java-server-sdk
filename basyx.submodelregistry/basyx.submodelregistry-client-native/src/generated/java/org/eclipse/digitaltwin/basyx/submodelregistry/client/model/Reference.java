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
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.Key;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ReferenceParent;
import org.eclipse.digitaltwin.basyx.submodelregistry.client.model.ReferenceTypes;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Reference
 */
@JsonPropertyOrder({
  Reference.JSON_PROPERTY_TYPE,
  Reference.JSON_PROPERTY_KEYS,
  Reference.JSON_PROPERTY_REFERRED_SEMANTIC_ID
})
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2023-07-18T15:02:01.566475800+02:00[Europe/Berlin]")
public class Reference {
  public static final String JSON_PROPERTY_TYPE = "type";
  private ReferenceTypes type;

  public static final String JSON_PROPERTY_KEYS = "keys";
  private List<Key> keys = new ArrayList<>();

  public static final String JSON_PROPERTY_REFERRED_SEMANTIC_ID = "referredSemanticId";
  private ReferenceParent referredSemanticId;

  public Reference() { 
  }

  public Reference type(ReferenceTypes type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public ReferenceTypes getType() {
    return type;
  }


  @JsonProperty(JSON_PROPERTY_TYPE)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setType(ReferenceTypes type) {
    this.type = type;
  }


  public Reference keys(List<Key> keys) {
    this.keys = keys;
    return this;
  }

  public Reference addKeysItem(Key keysItem) {
    if (this.keys == null) {
      this.keys = new ArrayList<>();
    }
    this.keys.add(keysItem);
    return this;
  }

   /**
   * Get keys
   * @return keys
  **/
  @javax.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_KEYS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public List<Key> getKeys() {
    return keys;
  }


  @JsonProperty(JSON_PROPERTY_KEYS)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setKeys(List<Key> keys) {
    this.keys = keys;
  }


  public Reference referredSemanticId(ReferenceParent referredSemanticId) {
    this.referredSemanticId = referredSemanticId;
    return this;
  }

   /**
   * Get referredSemanticId
   * @return referredSemanticId
  **/
  @javax.annotation.Nullable
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
   * Return true if this Reference object is equal to o.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Reference reference = (Reference) o;
    return Objects.equals(this.type, reference.type) &&
        Objects.equals(this.keys, reference.keys) &&
        Objects.equals(this.referredSemanticId, reference.referredSemanticId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, keys, referredSemanticId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Reference {\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
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

    // add `type` to the URL query string
    if (getType() != null) {
      joiner.add(String.format("%stype%s=%s", prefix, suffix, URLEncoder.encode(String.valueOf(getType()), StandardCharsets.UTF_8).replaceAll("\\+", "%20")));
    }

    // add `keys` to the URL query string
    if (getKeys() != null) {
      for (int i = 0; i < getKeys().size(); i++) {
        if (getKeys().get(i) != null) {
          joiner.add(getKeys().get(i).toUrlQueryString(String.format("%skeys%s%s", prefix, suffix,
          "".equals(suffix) ? "" : String.format("%s%d%s", containerPrefix, i, containerSuffix))));
        }
      }
    }

    // add `referredSemanticId` to the URL query string
    if (getReferredSemanticId() != null) {
      joiner.add(getReferredSemanticId().toUrlQueryString(prefix + "referredSemanticId" + suffix));
    }

    return joiner.toString();
  }
}

